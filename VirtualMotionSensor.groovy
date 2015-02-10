metadata {
  definition (name: "Virtual Motion Sensor", namespace: "Virtual", author: "justinlhudson") {
    capability "Motion Sensor"

    command "active"
    command "inactive"
  }

  tiles {
    standardTile("button", "device.motion", width: 2, height: 2, inactiveLabel: true, canChangeIcon: true) {
      state "active", label: "motion", action: "inactive", icon: "st.motion.motion.active", backgroundColor: "#53a7c0", nextState: "inactive"
      state "inactive", label: "no motion", action: "active", icon: "st.motion.motion.inactive", backgroundColor: "#ffffff", nextState: "active"
    }
    standardTile("refresh", "device.motion", inactiveLabel: false, decoration: "flat") {
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
  inactive()
}

def active() {
  sendEvent(name: 'motion', value: 'active')
}

def inactive() {
  sendEvent(name: 'motion', value: 'inactive')
}
