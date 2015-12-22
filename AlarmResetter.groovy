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
        input "reset", "number", title:"Reset (seconds)", defaultValue:30
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
    log.debug "alarms_strobe"
    def x = 3
  x.times { n ->
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != "strobe") {
          it.strobe()
        }
      }
      if( n > 0) {
        pause(3000)
      }
    }
}

def alarms_both() {
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

def alarms_off() {
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

def clear() {
    log.debug "clear"
    alarms_off()
    settings.alarms.each {
      if ( it != null && it.latestValue("alarm") != "off") {
        runIn(1500, clear, [overwrite: false])
        return
      }
    }
    state.lock = false
    sendNotificationEvent "Alarm(s) Reset..."
}

def alarmHandler(evt)
{
    log.debug "${evt.value}"
/*
    if(settings.siren == true && settings.strobe == true){
        settings.alarms*.both()
    }
    else if(settings.siren == true) {
        settings.alarms*.siren()
    }
    else if(settings.strobe == true){
        settings.alarms*.strobe()
    }
*/
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
