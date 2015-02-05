metadata {
  definition (name: "Virtual Contact", namespace: "Virtual", author: "justinlhudson") {
    capability "Contact Sensor"

    command "open"
    command "closed"
  }

  tiles {
    standardTile("button", "device.contact", width: 2, height: 2, canChangeBackground: true, inactiveLabel: true, canChangeIcon: true) {
      state "closed", label: '${name}', action: "open", icon: "st.contact.contact.closed", backgroundColor: "#79b821", nextState: "open"
      state "open",   label: '${name}', action: "closed", icon: "st.contact.contact.open",   backgroundColor: "#ffa81e", nextState: "closed"
    }
    standardTile("refresh", "device.contact", inactiveLabel: false, decoration: "flat") {
      state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
    }

    main "button"
    details(["button", "refresh"])
  }
}

def open() {
  sendEvent(name: "contact", value: "open")
}

def closed() {
  sendEvent(name: "contact", value: "closed")
}