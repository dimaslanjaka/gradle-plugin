const express = require("express");
const os = require("os");
const homedir = os.homedir();
const config = require("./config.json");
const port = config.port;
const path = require("path");
const rootdir = path.join(homedir, "/.m2/repository");
const app = express();
const serveIndex = require("./serve-index/index");
const ngrok = require("ngrok");
const fs = require("fs");
const auth = "1Szs4cJp7MoUlFPT3nyRjD5P05v_3BREWhqf8z2NdcNHMneUm";
const http = require("http");
const geoip = require("geoip-lite");
const serve = async function () {
	const url = await ngrok.connect({
		proto: "http", // http|tcp|tls, defaults to http
		addr: port, // port or network address, defaults to 80
		//auth: 'user:pwd', // http basic authentication for tunnel
		//subdomain: 'alex', // reserved tunnel name https://alex.ngrok.io
		authtoken: auth, // your authtoken from ngrok.com
		region: "us", // one of ngrok regions (us, eu, au, ap, sa, jp, in), defaults to us
		//configPath: '~/git/project/ngrok.yml', // custom path for ngrok config file
		//binPath: path => path.replace('app.asar', 'app.asar.unpacked'), // custom binary path, eg for prod in electron
		onStatusChange: (status) => {
			//console.log(status);
			//console.log(fs.readFileSync(__dirname + "/server.txt"), status);
		}, // 'closed' - connection is lost, 'connected' - reconnected
		onLogEvent: (data) => {
			//console.log(data);
		}, // returns stdout messages from ngrok process
	});
	fs.writeFileSync(__dirname + "/server.properties", "url=" + url);
	fs.writeFileSync(__dirname + "/server.txt", url);

	// send information to server
	/**
	 * @var {RequestOptions} options
	 */
	var options = {
		host: "backend.webmanajemen.com",
		port: 80,
		path: "/artifact/index.php",
		method: "POST",
		useragent:
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
	};

	var req = http.request(options, function (res) {
		console.log("STATUS: " + res.statusCode);
		//console.log("HEADERS: " + JSON.stringify(res.headers));
		res.setEncoding("utf8");
		res.on("data", function (chunk) {
			//console.log("BODY: " + chunk);
		});
	});

	req.on("error", function (e) {
		console.log("problem with request: " + e.message);
	});

	// write data to request body
	req.write("data\n");
	req.write("data\n");
	req.end();
};

app.set("trust proxy", true);
app.use(express.static(rootdir));
app.use(serveIndex(rootdir));
app.all("/*", async function requuestInformation(req, res, next) {
	var jsonbuilder = {};
	jsonbuilder.headers = req.headers;
	jsonbuilder.ip = req.ip;

	var geo = geoip.lookup(req.ip);
	jsonbuilder.useragent = req.headers["user-agent"];
	jsonbuilder.language = req.headers["accept-language"];
	jsonbuilder.country = geo ? geo.country : "Unknown";
	jsonbuilder.region = geo ? geo.region : "Unknown";
	if (!fs.existsSync(__dirname + `/info`)) {
		fs.mkdirSync(__dirname + `/info`, { recursive: true });
	}
	fs.writeFileSync(
		__dirname + `/info/${jsonbuilder.ip}.json`,
		JSON.stringify(jsonbuilder),
		{ encoding: "utf-8" }
	);

	//console.log(geo);
	next();
});
app.listen(port, serve);
