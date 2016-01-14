// Note: 20 sec (current cloud setting) timeout, watch the pause().

definition(
  name: "Alarm Resetter",
  namespace: "Operations",
  author: "justinlhudson",
  description: "Resets Alarming (incase someone comes invited without unlocking)",
  category:  "Safety & Security",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
  section("Alarming...") {
    input "alarms", "capability.alarm", title:"Reset alarms", multiple:true, required:true
    input "reset", "number", title:"Reset (seconds)", defaultValue:15
  }
}

def installed() {
  initialize()
}

def updated() {
  unsubscribe()
  initialize()
}

def resetHandler(evt)
{
  if (state.lock == false) {
    updated()
  }
}

def alarms_strobe() {
  try {
    log.debug "alarms_strobe"
    def x = 6
    x.times { n ->
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "strobe") {
          it.strobe()
        }
      }
      if( n > 0) {
        pause(1500)
      }
    } 
  }
  catch (all) {
    log.error "Something went horribly wrong!\n${all}"
  }
}

def alarms_both() {
  try {
    log.debug "alarms_both"
    def x = 6
    x.times { n ->
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "both") {
          it.both()
        }
      }
      if( n > 0) {
        pause(1500)
      }
    } 
  }
  catch (all) {
    log.error "Something went horribly wrong!\n${all}"
  }
}


def alarms_off() {
  try {
    log.debug "alarms_off"
    def x = 6
    x.times { n ->
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "off") {
          it.off()
        }
      }
      if ( n > 0 ) {
        pause(1500)
      }
    }
  }
  catch (all) {
    log.error "Something went horribly wrong!\n${all}"
  }
}

def clear() {
  log.debug "clear"
  state.lock = false  // rather reset at end of function, but incase things go to poo as they have in the past...

  alarms_off()  
  // last ditch effort, keep calling clear until really cleared (no timeout since schedualed)
  try {
    settings.alarms.each {
      if ( it != null && it.latestValue("alarm") != "off") {
        log.debug "wtf"
        runIn(1500, clear, [overwrite: false])
        return
      }
  }
  } catch (all) {
    log.error "Something went horribly wrong!\n${all}"
  }

    sendNotificationEvent "Alarm(s) Reset..."
  }

  def alarmHandler(evt)
  {
    log.debug "${evt.value}"
    if( evt.value != "off" && state.lock == false) {
      state.lock = true
      if(evt.value == "strobe") {
        alarms_strobe()
      }
      else {
        alarms_both()
      }

      sendNotificationEvent "Alarm(s) Active!"
      runIn(settings.reset, clear, [overwrite: true])
    }
  }

private def initialize() {
  state.lock = false
  subscribe(alarms, "alarm", alarmHandler)

  // HACK: keep alive
  subscribe(location, "sunset", resetHandler)
  subscribe(location, "sunrise", resetHandler)
}
