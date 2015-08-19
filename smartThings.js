//var config = require('./config');
var request = require('request');

var config = {};

config.ST_AUTH =  "Bearer <access_token>"
config.ST_APP = "https://graph.api.smartthings.com/api/smartapps/installations/<endpoint>" // change to use your oauth endpoint

module.exports = {
  get= function(endpoint, cb) {
    request({
      "headers": {
          "Content-Type": "application/json",
          "Authorization": config.ST_AUTH
       },
      "uri": config.ST_APP + "/" + endpoint,
      method: "GET",
      followRedirect:true
    }, cb);
  },

  put = function(endpoint, bodyStr, cb) {
      request({
        "headers": {
          "Content-Type": "application/json", 
          "Authorization": config.ST_AUTH
        },
        "uri": config.ST_APP + "/" + endpoint,
        method: "PUT",
        body: bodyStr,
        followRedirect:true
      }, cb);
  }

};
