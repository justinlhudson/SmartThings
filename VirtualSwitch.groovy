metadata {
  definition (name: "Virtual Switch", namespace: "Virtual", author: "justinlhudson") {
    capability "Switch"

    command "on"
    command "off"
  }

  tiles {
    standardTile("button", "device.switch", width: 2, height: 2, inactiveLabel: true, canChangeIcon: true) {
      state "off", label: "off", action: "on", icon: "st.switch.switch.on", backgroundColor: "#ffffff", nextState: "on"
      state "on", label: "on", action: "off", icon: "st.switch.switch.off", backgroundColor: "#79b821", nextState: "off"
    }
    standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
    }

    main "button"
    details(["button", "refresh"])
  }
}

def installed() {
  initialize()
}

def updated() {
  unschedule()
  initialize()
}

private def initialize() {
  off()
}

def on() {
  sendEvent(name: "switch", value: "on")
}

def off() {
  sendEvent(name: "switch", value: "off")
}
