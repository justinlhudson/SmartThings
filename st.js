use Web-Application
show collections

print("")

var _keys = Object.keys(db.logs.findOne())

print("Keys:")
print(_keys)

var _names = db.logs.distinct("name")
var _types = db.logs.distinct("type")

print("Names:")
print(_names)
print("Types:")
print(_types)

function notSeen(minutes) {
  var seen = []
  _names.forEach(function(name) {
    // {"type": {$ne: "temperature"}}
    var createdDate = new Date(ISODate().getTime() - (1000 * 60 * minutes))
    var newest = db.logs.find({ $and: [ {"name": name},{"created": {$gte: createdDate }} ] }).sort({"created":-1}).limit(1).toArray()[0]

    if (newest != undefined) {
      seen.push(newest)
      //printjson(newest)
    }

  });

  var seenNames = seen.map(function(obj) {
    return obj.name
  });

  var result = []
  _names.forEach(function(name) {
    if(!seenNames.includes(name)) {
      result.push(name)
    }
  });

  return result
}

// Todo:  add battery % checker!!!  not based on days, but last seen period

var _hours=1440
print("")
print("*** NOT Seen ("+_hours+" hrs.) ***")
print("")
notSeen(_hours).forEach(function(name){
  print(name)
});

function batteryReplace(percent) {
  var seen = []
  _names.forEach(function(name) {
    var newest = db.logs.find({ $and: [ {"name": name},{"type": "battery"}]}).sort({"created":-1}).limit(1).toArray()[0]

    if (newest != undefined) {
      seen.push(newest)
      //printjson(newest)
    }

  });

  var result = []
  seen.forEach(function(item) {
    if(parseInt(item.value) <= percent) {
      result.push(item.name)
    }
  });

  return result
}

var _percent=40
print("")
print("*** Battery Low (<="+_percent+"%) ***")
print("")
batteryReplace(_percent).forEach(function(name){
  print(name)
});

print("");
