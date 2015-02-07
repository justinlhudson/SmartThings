metadata {
  definition (name: "Virtual Presence", namespace: "Virtual", author: "justinlhudson") {
    capability "Presence Sensor"
    capability "Sensor"

    command "away"
    command "present"
  }

  tiles {
    standardTile("button", "device.presence", width: 2, height: 2, inactiveLabel: true, canChangeIcon: true) {
      state "present", label: "${name}", action: "away", icon: "st.presence.tile.mobile-present", backgroundColor: "#53a7c0", nextState: "not present"
      state "not present", label: "${name}", action: "present", icon: "st.presence.tile.mobile-not-present", backgroundColor: "#ffffff", nextState: "present"
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
  away()
}

def away() {
  sendEvent(name: 'presence', value: 'not present')
}

def present() {
  sendEvent(name: 'presence', value: 'present')
}

