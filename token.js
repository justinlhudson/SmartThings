// http://smartthings.readthedocs.org/en/latest/smartapp-web-services-developers-guide/tutorial-part1.html
// https://community.smartthings.com/t/tutorial-creating-a-rest-smartapp-endpoint
// https://community.smartthings.com/t/oauth-access-token-expiry-and-refresh-token-api/2741
// https://community.smartthings.com/t/amazon-echo-developers-access/10701

// command line (node <app>.js <id> <secret> )
var CLIENT_ID = process.argv[2];
var CLIENT_SECRET = process.argv[3];

var request = require('/usr/local/lib/node_modules/request');
var express = require('/usr/local/lib/node_modules/express'),

app = express();

app.set('port', 3000);

var oauth2 = require('/usr/local/lib/node_modules/simple-oauth2')({
  clientID: CLIENT_ID,
  clientSecret: CLIENT_SECRET,
  site: 'https://graph.api.smartthings.com'
});
 
// Authorization uri definition 
var authorization_uri = oauth2.authCode.authorizeURL({
  redirect_uri: 'http://localhost:'+ app.get('port') +'/callback',
  scope: 'app'
});

app.get('/auth', function (req, res) {
  res.redirect(authorization_uri);
});

app.get('/', function (req, res) {
  res.send('<a href="/auth">Login with SmartThings</a>');
});

// Callback service parsing the authorization token and asking for the access token 
app.get('/callback', function (req, res) {
  var code = req.query.code;
  oauth2.authCode.getToken({
    code: code,
    redirect_uri: 'http://localhost:'+ app.get('port') +'/callback'
  }, saveToken);

  function saveToken(error, result) {
    if (error) { console.log('Access Token Error', error.message); }

    // result.access_token is the token, get the endpoint
    var bearer = result.access_token
    var sendreq = { method: "GET", uri: 'https://graph.api.smartthings.com/api/smartapps/endpoints' + "?access_token=" + result.access_token };
    request(sendreq, function (err, res1, body) {
      var endpoints = JSON.parse(body);
      var access_url = endpoints[0].url // last one (not all if any)

      console.log(bearer);
      console.log(endpoints);

      res.send('<pre>https://graph.api.smartthings.com/' + access_url + '</pre><br><pre>Bearer ' + bearer + '</pre>');
    });
  }
});

app.listen(app.get('port'), function() {
  console.log('Node listening on port: ' + app.get('port'))
});
