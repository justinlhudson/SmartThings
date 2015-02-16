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
//        input "siren", "bool", title:"Siren?", description: "Stobe still?", defaultValue: true
        input "delay", "number", title:"Active (seconds)", defaultValue:60
        input "reset", "number", title:"Reset (minutes)", defaultValue:180
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
    state.alarmActive = false
 
    settings.persons*.away()
    settings.alarms*.off()

    sendNotificationEvent "Alarm(s) Enabled!"
}

def set() {
    settings.persons*.present()
    
    settings.alarms*.off()
    if(settings.strobe == true) {
      settings.alarms*.strobe()
    }
    
    sendNotificationEvent "Alarm(s) Disabled!"

    def now = new Date()
    def runTime = new Date(now.getTime() + (settings.reset * 1000 * 60))
    runOnce(runTime, clear, [overwrite: true])
    //sendPushMessage
}

def alarmHandler(evt)
{
    log.debug "${evt.value}"
    if( evt.value != "off" && state.alarmActive == false) {
        state.alarmActive = true
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
        def now = new Date()
        def runTime = new Date(now.getTime() + (settings.delay * 1000))
        runOnce(runTime, set, [overwrite: true])
    }
    
}

private def initialize() {
      state.alarmActive = false
      subscribe(alarms, "alarm", alarmHandler)
}