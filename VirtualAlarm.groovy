metadata {
 definition (name: "Virtual Alarm", namespace: "Virtual", author: "justinlhudson") {
  capability "Alarm"
 }

 tiles {
  standardTile("alarm", "device.alarm",canChangeBackground: true, inactiveLabel: true, canChangeIcon: true, width: 2, height: 2) {
    state "off", label:'off', action:'both', icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff", nextState: "both"
    state "both", label:'alarm!', action:'off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13", nextState: "off"
}
  standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat") {
    state "default", label:'', action:"off", icon:"st.secondary.off"
  }

  main "alarm"
  details(["alarm", "off"])
 }
}

def off()
{
  sendEvent(name: "alarm", value: "off")
}

def strobe() {
  sendEvent(name: "alarm", value: "strobe")
}

def siren() {
  sendEvent(name: "alarm", value: "siren")
}

def both() {
    sendEvent(name: "alarm", value: "both")
}