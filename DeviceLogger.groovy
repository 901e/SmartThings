definition (
    name: "DeviceLogger",
    namespace: "Operations",
    author: "justinlhudson",
    description: "Logger",
    category: "Convenience",
    iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Electronics.electronics13-icn?displaySize",
    iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Electronics.electronics13-icn?displaySize=2x")

preferences {
    section("Log devices...") {
        input "powers", "capability.powerMeter", title: "Power", required: false, multiple: true
        input "energies", "capability.energyMeter", title: "Energy", required: false, multiple: true
        input "contacts", "capability.contactSensor", title: "Contact", required: false, multiple: true
        input "switches", "capability.switch", title: "Switch", required: false, multiple: true
        input "motions", "capability.motionSensor", title: "Motion", required: false, multiple: true
        input "batteries", "capability.battery", title: "Battery", required: false, multiple: true
        input "temperatures", "capability.temperatureMeasurement", title: "Temperature", required:false, multiple: true
        input "thermostats", "capability.thermostat", title: "Thermostat", required:false, multiple: true
        input "humidities", "capability.relativeHumidityMeasurement", title: "Humidity", required: false, multiple: true
        input "illuminances", "capability.illuminanceMeasurement", title: "Illuminance", required:false, multiple: true
    }

    section ("API (GET request query") {
    input("ip", "string", title:"IP Address", description: "IP Address", required: true, displayDuringSetup: true)
    input("port", "string", title:"Port", description: "Port", defaultValue: 3000 , required: true, displayDuringSetup: true)
    input("path", "string", title:"Path", description: "Path", defaultValue: "/api/log" , required: true, displayDuringSetup: true)
    }

}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(temperatures, "temperature", handleTemperatureEvent)
    subscribe(thermostats, "thermostatOperatingState", handleThermostatEvent)
    subscribe(humidities, "humidity", handleHumidityEvent)
    subscribe(illuminances, "illuminance", handleIlluminanceEvent)
    subscribe(batteries, "battery", handleBatteryEvent)
    subscribe(contacts, "contact", handleContactEvent)
    subscribe(powers, "power", handlePowerEvent)
    subscribe(energies, "energy", handleEnergyEvent)
    subscribe(motions, "motion", handleMotionEvent)
    subscribe(switches, "switch", handleSwitchEvent)
}

def handleIlluminanceEvent(evt) {
    logField("illuminance",evt) { it.toString() }
}

def handleThermostatEvent(evt) {
    logField("thermostat",evt) { ((it == "heating" ? 1 : 0) || (it == "cooling" ? 1 : 0)).toString() }
}

def handlePowerEvent(evt) {
    logField("power",evt) { it.toString() }
}

def handleEnergyEvent(evt) {
    logField("energy",evt) { it.toString() }
}

def handleBatteryEvent(evt) {
    logField("battery",evt) { it.toString() }
}

def handleHumidityEvent(evt) {
    logField("humidity",evt) { it.toString() }
}

def handleTemperatureEvent(evt) {
    logField("temperature",evt) { it.toString() }
}

def handleContactEvent(evt) {
    logField("contact",evt) { it == "open" ? "1" : "0" }
}

def handleMotionEvent(evt) {
    logField("motion",evt) { it == "active" ? "1" : "0" }
}

def handleSwitchEvent(evt) {
    logField("switch",evt) { it == "on" ? "1" : "0" }
}

private logField(type, evt, Closure c) {
    def name = evt.displayName.trim()
    def value = c(evt.value)

    log.debug "Logging: ${name}, ${type}, ${value}"
    def result = new physicalgraph.device.HubAction(
        method: "PUT",
        path: "${path}?type=${type}&name=${name}&value=${value}",
        headers: [
            HOST: getHostAddress()
        ]
    )
    log.debug "${result}"
    sendHubCommand(result)
}

private getHostAddress() {
  return "${ip}:${port}"
}