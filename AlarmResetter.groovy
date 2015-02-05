definition(
    name: "AlarmResetter",
    namespace: "Operations",
    author: "justinlhudson",
    description: "Resets Alarming after seen on the next go round",
    category:  "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Alarming") {
        input "alarms", "capability.alarm", title:"Reset these alarms", multiple:true, required:true
        input "interval", "number", title:"Reset in (~minutes, max 2x)", defaultValue:1
        input "contacts", "capability.contact", title:"Set contacts on (used to trigger mode)", multiple:true, required:false
        input("strobe", "bool", title:"Strobe?", description: "Stobe still?", defaultValue: true)
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def pollingTask() {
    if(state.flag == 1) {
        state.flag = 0

        resetAlarms()
        setContacts()
    }
    else if(state.flag == 0) {
      state.flag = 1
    }
}

private setContacts() {
    settings.contacts*.open()
}

private resetAlarms() {
    for (device in settings.alarms) {
        if(device.currentValue("switch") == "on") {
            device.off()
            if(strobe == true) {
                device.strobe()
            }
        }
      }
}

private def initialize() {
    state.flag = 0 // reset

    def minutes = settings."interval".toInteger()
    def rate = "0 0/${minutes} * * * ?"
    schedule(rate, pollingTask)
}