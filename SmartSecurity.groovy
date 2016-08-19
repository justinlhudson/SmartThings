/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Smart Security
 *
 *  Author: SmartThings
 *  Original Date: 2013-03-07
 *
 *  Modified by: Justin L. Hudson
 */
definition(
    name: "Smart Security*",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Alerts you when there are intruders but not when you just got up for a glass of water in the middle of the night",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-IsItSafe.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-IsItSafe@2x.png"
)

preferences {
  section("Sensors detecting an intruder") {
    input "intrusionMotions", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false
    input "intrusionContacts", "capability.contactSensor", title: "Contact Sensors", multiple: true, required: false
  }
  section("Sensors detecting residents") {
    input "residentMotions", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false
  }
  section("Alarm settings and actions") {
    input "alarms", "capability.alarm", title: "Which Alarm(s)", multiple: true, required: false
    input "silent", "enum", options: ["Yes","No"], title: "Silent alarm only (Yes/No), i.e. strobe"
    //input "seconds", "number", title: "Delay in seconds before siren sounds"
    input "lights", "capability.switch", title: "Flash lights)", multiple: true, required: false
    input "switches", "capability.switch", title: "Turn switches on", required: false, multiple: true
    input "newMode", "mode", title: "Change to this mode", required: false
    input "clear", "number", title:"Active (seconds)", defaultValue:0
  }
  section("Notify others (optional)") {
    input "textMessage", "text", title: "Send this message", multiple: false, required: false
        input("recipients", "contact", title: "Send notifications to") {
            input "phone1", "phone", title: "To this phone", multiple: false, required: false
            input "phone2", "phone", title: "To this phone", multiple: false, required: false
            input "phone3", "phone", title: "To this phone", multiple: false, required: false
        }
  }
  section("Arm system when residents quiet for (default 30 seconds)") {
    input "residentsQuietThreshold", "number", title: "Time in seconds", required: false
  }
}

def installed() {
  log.debug "INSTALLED"
  updated()
}

def updated() {
  log.debug "UPDATED"

  init()
  runIn(10, init, [overwrite: true])
}

def init() {
  unsubscribe()
  unschedule()

  subscribeToEvents()
  state.alarmActive = null
  state.residentsAreUp = null
  state.lastIntruderMotion = null

  //clear()

  state.check = true
}

private def switches_on() {
    log.debug "switches_on"
    def x = 6
    x.times { n ->
      settings.switches.each {
        if ( it != null && it.currentSwitch != "on") {
          it.on()
        }
      }
      if( n > 0) {
        pause(500)
      }
    }
}

private def switches_off() {
    log.debug "switches_off"
    def x = 6
    x.times { n ->
      settings.switches.each {
        if ( it != null && it.currentSwitch != "off") {
          it.on()
        }
      }
      if( n > 0) {
        pause(500)
      }
    }
}

private subscribeToEvents()
{
  subscribe(location, "mode", modeHandler)
  subscribe intrusionMotions, "motion", intruderMotion
  subscribe residentMotions, "motion", residentMotion
  subscribe intrusionContacts, "contact", contact
  subscribe alarms, "alarm", alarm
  subscribe(app, appTouch)
}

def modeHandler(evt)
{
  // force reset, HACK: for lookups & app stopping?
  if(evt.value != newMode) {
    updated()
  }
}

private residentsHaveBeenQuiet()
{
  def threshold = ((residentsQuietThreshold != null && residentsQuietThreshold != "") ? residentsQuietThreshold : 30) * 1000
  def result = true
  def t0 = new Date(now() - threshold)
  for (sensor in residentMotions) {
    def recentStates = sensor.statesSince("motion", t0)
    if (recentStates.find{it.value == "active"}) {
      result = false
      break
    }
  }
  log.debug "residentsHaveBeenQuiet: $result"
  result
}

private intruderMotionInactive()
{
  def result = true
  for (sensor in intrusionMotions) {
    if (sensor.currentMotion == "active") {
      result = false
      break
    }
  }
  result
}

def alarms_strobe() {
  log.debug "alarms_strobe"
  def x = 3
  x.times { n ->
    try {
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "strobe") {
          it.strobe()
        }
      }
    }
    catch (all) {
      log.error "Something went horribly wrong!\n${all}"
    }
    if( n > 0) {
      pause(1500)
    }
  }
}

def alarms_both() {
  log.debug "alarms_both"
  def x = 18
  x.times { n ->
    try {
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "both") {
          it.both()
        }
      }
    }
    catch (all) {
      log.error "Something went horribly wrong!\n${all}"
    }
    if( n > 0) {
      pause(500)
    }
  }
}

def alarms_off() {
  log.debug "alarms_strobe"
  def x = 3
  x.times { n ->
    try {
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "off") {
          it.off()
        }
      }
    }
    catch (all) {
      log.error "Something went horribly wrong!\n${all}"
    }
    if( n > 0) {
      pause(1500)
    }
  }
}

private isResidentMotionSensor(evt)
{
  residentMotions?.find{it.id == evt.deviceId} != null
}

def appTouch(evt)
{
  clear()
}

// Here to handle old subscriptions
def motion(evt)
{
  if (isResidentMotionSensor(evt)) {
    log.debug "resident motion, $evt.name: $evt.value"
    residentMotion(evt)
  }
  else {
  /*
    if(state.check == true) {
      log.debug "state = true"
      for (sensor in residentMotions) {
        device.refresh()
        log.debug "refresh resident"
      }
      state.check = false
      return
    }
   */
    log.debug "intruder motion, $evt.name: $evt.value"
    intruderMotion(evt)
/*
    log.debug "state = false"
    state.check = true
*/
  }
}

def intruderMotion(evt)
{
  if (evt.value == "active") {
    log.debug "motion by potential intruder, residentsAreUp: $state.residentsAreUp"
    if (!state.residentsAreUp) {
      log.trace "checking if residents have been quiet"
      if (residentsHaveBeenQuiet()) {
        log.trace "calling startAlarmSequence"
        startAlarmSequence()
      }
      else {
        log.trace "calling disarmIntrusionDetection"
        disarmIntrusionDetection()
      }
    }
  }
  state.lastIntruderMotion = now()
}

def residentMotion(evt)
{
  // Don't think we need this any more
  //if (evt.value == "inactive") {
  //  if (state.residentsAreUp) {
  //      startReArmSequence()
  //    }
  //}
  if (newMode) {
    setLocationMode(newMode)
    log.debug "change mode"
  }
}

def contact(evt)
{
  if (evt.value == "open") {
    // TODO - check for residents being up?
    if (!state.residentsAreUp) {
      if (residentsHaveBeenQuiet()) {
        startAlarmSequence()
      }
      else {
        disarmIntrusionDetection()
      }
    }
  }
}

def alarm(evt)
{
  log.debug "$evt.name: $evt.value"
  if (evt.value == "off") {
    clear()
  }
}

private disarmIntrusionDetection()
{
  log.debug "residents are up, disarming intrusion detection"
  state.residentsAreUp = true
  scheduleReArmCheck()
}

private scheduleReArmCheck()
{
  def cron = "0 * * * * ?"
  schedule(cron, "checkForReArm")
  log.debug "Starting re-arm check, cron: $cron"
}

def checkForReArm()
{
  def threshold = ((residentsQuietThreshold != null && residentsQuietThreshold != "") ? residentsQuietThreshold : 3) * 60 * 1000
  log.debug "checkForReArm: threshold is $threshold"
  // check last intruder motion
  def lastIntruderMotion = state.lastIntruderMotion
  log.debug "checkForReArm: lastIntruderMotion=$lastIntruderMotion"
  if (lastIntruderMotion != null)
  {
    log.debug "checkForReArm, time since last intruder motion: ${now() - lastIntruderMotion}"
    if (now() - lastIntruderMotion > threshold) {
      log.debug "re-arming intrusion detection"
      state.residentsAreUp = false
      unschedule()
    }
  }
  else {
    log.warn "checkForReArm: lastIntruderMotion was null, unable to check for re-arming intrusion detection"
  } 
}

private startAlarmSequence()
{
  if (state.alarmActive) {
    log.debug "alarm already active"
  }
  else {
    state.alarmActive = true
    log.debug "starting alarm sequence"

    sendNotificationEvent("Potential intruder detected!")

    if (location.contactBookEnabled) {
        sendNotificationToContacts(textMessage ?: "Potential intruder detected", recipients)
    }
    else {
        if (settings.phone1) {
            sendSms(settings.phone1, textMessage ?: "Potential intruder detected")
        }
        if (settings.phone2) {
            sendSms(settings.phone2, textMessage ?: "Potential intruder detected")
        }
        if (settings.phone3) {
            sendSms(settings.phone3, textMessage ?: "Potential intruder detected")
        }
    }

    if (silentAlarm()) {
      log.debug "Silent alarm only"
      alarms_strobe()
    }
    else {
      /*
      def delayTime = seconds
      if (delayTime) {
        alarms?.strobe()
        runIn(delayTime, "soundSiren")
        log.debug "Sounding siren in $delayTime seconds"
      }
      else {
        */
        alarms_both()
      //}
    }

    if(switches) {
      switches_on()
    }

    if (lights) {
      flashLights(Math.min((seconds/2) as Integer, 10))
    }

    // Note: at end of sequence (for reset)!
    if (newMode) {
      setLocationMode(newMode)
    }

    if (settings.clear && settings.clear > 0 ) {
      runIn(settings.clear, clear, [overwrite: true])
    }
  }
}

def clear() {
    state.alarmActive = false
    alarms_off()
    switches_off()
}

def continueFlashing()
{
  unschedule()
  if (state.alarmActive) {
    flashLights(10)
    schedule(util.cronExpression(now() + 10000), "continueFlashing")
  }
}

private flashLights(numFlashes) {
  def onFor = 1000
  def offFor = 1000

  log.debug "FLASHING $numFlashes times"
  def delay = 1L
  numFlashes.times {
    log.trace "Switch on after  $delay msec"
    lights?.on(delay: delay)
    delay += onFor
    log.trace "Switch off after $delay msec"
    lights?.off(delay: delay)
    delay += offFor
  }
}

private silentAlarm()
{
  silent?.toLowerCase() in ["yes","true","y"]
}
