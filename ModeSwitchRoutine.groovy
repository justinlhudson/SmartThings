definition(
  name: "Mode Switch Routine",
  namespace: "Operations",
  author: "justinlhudson",
  description: "When mode changes activate routine",
  category: "Convenience",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
  page(name: "getPref")
}
  
def getPref() { 
    dynamicPage(name: "getPref", install:true, uninstall: true) {
    section("Choose Mode to use...") {
      input "mode", "mode", title: "Mode Activate", required: true
    }

    // get the available actions
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
            // sort them alphabetically
            actions.sort()
                    section("Routine") {
                            log.trace actions
                // use the actions as the options for an enum input
                input "action", "enum", title: "Select an action to execute", options: actions
                    }
            }
    section([mobileOnly:true], "Options") {
      label(title: "Assign a name", required: false)
    }
  }
}

def installed() {
  log.debug "Installed with settings: ${settings}"
  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"
  unsubscribe()
  unschedule()

  initialize()
}

def modeHandler(evt)
{
  if(evt.value == settings.mode) {
    location.helloHome.execute(settings.action)
  }
}

private def initialize() {
  subscribe(location, "mode", modeHandler)
}

/*
def changeMode(newMode) {
  if (location.mode != newMode) {
    if (location.modes?.find{it.name == newMode}) {
      setLocationMode(newMode)
    } else {
      log.debug "Unable to change to undefined mode '${newMode}'"
    }
  }
}
*/
