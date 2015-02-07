definition(
    name: "AlarmResetter",
    namespace: "Operations",
    author: "justinlhudson",
    description: "Resets Alarming (incase someone comes invited without unlocking)",
    category:  "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Alarming") {
        input "alarms", "capability.alarm", title:"Reset alarms", multiple:true, required:true
        input "strobe", "bool", title:"Strobe?", description: "Stobe still?", defaultValue: true
        input "interval", "number", title:"Reset (seconds)", defaultValue:60
        input "persons", "capability.presenceSensor", title:"Set present (e.g. Virtual)", multiple:true, required:false
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    unschedule()
    
    initialize()
}

def clear() {
    settings.persons*.away()
    settings.alarms*.off()
}

def set() {
    settings.persons*.present()
    
    settings.alarms*.off()
    if(settings.strobe == true) {
      settings.alarms*.strobe()
    }
    
    sendNotificationEvent "Alarms Disabled!"
    //sendPushMessage
}

def alarmHandler(evt)
{
    log.debug "${evt.value}"
    if( evt.value != "off") {
        def now = new Date()
        def runTime = new Date(now.getTime() + (settings.interval * 1000))
        runOnce(runTime, set, [overwrite: true])
    }
    
}

private def initialize() {
      subscribe(alarms, "alarm", alarmHandler)
}