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
        input "alarmsEvent", "capability.alarm", title:"Alarm Master", multiple:false, required:true
        input "alarms", "capability.alarm", title:"Alarm Slave", multiple:true, required:true
        input "strobe", "bool", title:"Strobe?", description: "Stobe still?", defaultValue: true
        input "delay", "number", title:"Active (seconds)", defaultValue:60
        input "reset", "number", title:"Reset (seconds)", defaultValue:60
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def alarms_match(match) {
    log.debug "alarms_match->"+match
    def x = 6
     x.times {
      settings.alarms.each {
        if ( it != null && it.latestValue("alarm") != match) {
          if (match == "both") {
            it.both()
          }
          else if (match == "strobe") {
            it.strobe()
          }
          else {
            it.off()
          }
        }
      }
      if( n > 0) {
        pause(1500)
      }
    }
}

def clear() {
    log.debug "clear"
    alarms_match(alarms,"off")
    alarms_match(alarmsEvent,"off")
    sendNotificationEvent "Alarm(s) Reset..."
}

def set() {
  log.debug "set"

  if(settings.strobe == true) {
    alarms_match(alarms,"strobe")
    alarms_match(alarmsEvent,"strobe")

    runIn(settings.reset, clear, [overwrite: true])
    sendNotificationEvent "Alarm(s) Silented!"
  }
  else
  {
    clear()
  }
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
    if( evt.value != "off") {
      alarms_match(alarms,evt.value)

      runIn(settings.delay, set, [overwrite: true])
      sendNotificationEvent "Alarm(s) Active!"
    }
}

private def initialize() {
      subscribe(alarmsEvent, "alarm", alarmHandler)
}
