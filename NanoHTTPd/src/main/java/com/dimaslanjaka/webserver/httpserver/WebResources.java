package com.dimaslanjaka.webserver.httpserver;

/**
 * This class contains resources used in error-pages and the directory-listing (CSS, Header, Footer, ...)
 *
 * Icons (base64-encoded): created by Yannick Lung (yanlu.de)
 */
public class WebResources {
	private static  String ICON_DIRECTORY = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAASCAYAAABB7B6eAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wQSCxYrnF2e+AAAAIRJREFUOMvtkcEJQjEQRGc23rQILcFGLMTwPdmIC2nEhgQ7EI+SjCX8rBDwkHce5s2ydPeLpBtJrPBKKR1zzg8EMAA95SC5q7UuCGK9QUkJwKmUcogI6O4K5BuAJ4A3AK4M+pC8bn64eN+1nERr7W4YiJlthwpCT56CKZiCPxcskkb1n7/fYCQICSvDCQAAAABJRU5ErkJggg==";
	private static  String ICON_DIRECTORY_FULL = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAvklEQVRYR+2Wyw3CMAyG7e7AYxzYALED3OJkFueAGAOxAIzT7hCDcqlaiUNTIyUH5+zHl0+y9CNUflh5PxiAGWjHADOfReTWdd126WUg4tE591pa/6tuNMDMAwBsCoe9iehQ2DMrnwJ81gzSWlADAIDKwj8AisWJyICIV+/9owpAJs4QIYRdNYAMQURoAGbADJgBM2AGmjKwJpIVZ4FJQ09E+9FAjPGUUrqXhFLF9h4ALkT0bCeWK36jaq1u4AvLJG4hpiQTTAAAAABJRU5ErkJggg==";
	private static  String ICON_FILE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA0AAAASCAYAAACAa1QyAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wQSCxk7BnKSUwAAAMpJREFUKM/tkDFKA1EURe+975Ng8wux0iIGJLWNOxDdgOAuLATrcfqBAXfhAlJkN65hrBz0XQstBEMmreCB193Dgce+758kXQGY4zevmfmYmR6GYd227QgAknQNYEXydNtJOpJ0X2u9aZpmBgACMCNJ7EDSWSnlrtZ6CYAFE9gWyQ8AFxHx0HXdUCaEsL3MzOeIOATwHhEnmggdkLyNiGMAIFkkne8s8YsFgMWP+stUaftj/qU/I422vc/4ezeWzNxIsu35Ht6b7c0noYFDofiUTmIAAAAASUVORK5CYII=";
	private static  String ICON_BACK = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAASCAYAAABB7B6eAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wQSCxsckU5FugAAAQdJREFUOMu1lDFOw1AQRN+ODZYQUFJDSclFchGLuCA3SEUHtlJS5Qacgzsg0VKhKIUx3qUJZWT/BI/0q53/R7N/dq1pmioinhjGV5Zld2VZvpMAjXwcMzvv+/6eRGgsMSIyYLZarW5SBKyu60jgO/ABbAAb4P6Y2SI/wPF1gutXMS3OcuAR6CYSOMmBW6CdSKDIgdmUPUr55O+IeN7NRAWc/qdAuHtdFMUSoG1bk/QwIqrDAhFBRMwlfXZdtwCQ9ObuczNrzOy4SZZUSdoCa+Bqd9aStpKqox24+9LMLnbt6P/iB7y4+2bIQT5iyV3uL+2tpS+7QzGpQESgiJgn3Dkdm/+IQFL5C/KXV8h2WypQAAAAAElFTkSuQmCC";
	//private static  String ICON_WARNING = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABUAAAASCAYAAAC0EpUuAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wQSCx4Lb+o0OAAAAe9JREFUOMullEGLUlEUx//nvmuSGbooilYlDVEEzdCsBmKofRsZCIlyYYsQwkSGgqEm2g72vProIWbwNvoZWgh9gWnnLlxHVBgKKvPuOy1G6+U4T63/5sI95/7OuefccwkLyDRNIxwOnxmNRsN8Pv9znr8IMhIRAEBK+cB13S9CiI+2bV/4LygzwzTNy8y8Ow6y6rruk3q9bvwz1LZtKaV8DOCiL9Dzfr9/aWmo4zgAAK31NWYuzCiLszQ0nU5PoM6fBNmXLG+Uy+UCABQKhfnQZrMJAFBKvSai1TGFAKwT0d5hogQAzyqVypVisTgfmkqloJS6SkRPfduDXC63z8z7vr2zWuudVqsl5kJLpVKIiF4COO0v49R6eFiIrXa7vTkXKoTYBLCFxXQSgAkAiUSCjkCTyeSks28BSCyuG0qpF51OhxuNxtHrKKVeEdHujCE4CIVCUdd17wN4P20GQFLKlWw2+/kvqGVZa57nfQoY2XfMvAbg5jH2D5FI5G4mkzkQAFCr1U54nvcmeGL50RjIxzjc6fV6yd81HQwG9wDcCvpb4vF4jIi2p0vmU0gIsVOtVmOGZVkxZq4DOB/UjeFw+APAbQArAW7ntNZfpWEYp7TW1yffXID2FnwNKRmNRr93u13FzA+XfEqz9A3A9i91qrmQbGpbBAAAAABJRU5ErkJggg==";
	private static  String ICON_WARNING_FULL = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACrElEQVRYR+2WMWgUQRSG35tbPRALPcROEysjCookoCiIYCpFGwURbBQNmNvZLJyF2GxhIYqJM3vhOAsFC0VQ0MJG0SoWFkosDNhoREEULwjGQJKdN7KShPXc3Zvdi6TJljv//O+bf2dmH8ISP7jE9WEZoK0EtNaIiLqdz5gboFqt9hLRHa31F8uyDvb393/KA5ILYHBwsGRZ1jsAWDdX9Jlt27150sgFIKW8AQBnmlZ8gnN+N2sKmQF839+jtR6JKfRVKdXluu6PLBCZAOr1+orp6enXALAtoUiNc37uvwFIKc8DwJWkAkQUnojdAwMDL00hjBOoVqsdQRCMMcZWzZsT0RRjbCUAWPPvEHG00Wj0eJ4XmEAYA0gpHwHA4UjxSSLqsCxrJwA8bSrmcs6vLxqAEOIIIj6MGiqlPrqu2zk0NNRZKBQ+RMeIaJIxtoVz/rkVRMsEhoeHVyulxgBggylAqNNaP3Ac52jbAL7vX9VaV5qN0hKIaA9xzh+nQaQmIKXcTkSvGGOFnADjxWJxa19f31QSRCKA53msVCq9AIBdcZMNEwg/xWXHcS5kBhBCnEXEetLEeQDf9zdprd+nxBwwxnaUy+W3cZrYBGq12vrZ2dnwZ7MmxXgGAA5orXsQ8VqLzTYyMTGxz/M8atbFAkgpbwPAyVY7OMs4Ip6ybftWSwAhxH5EfG5o/gsAitGbMOWabgRB0FWpVL5HNX8lIKUMzd4AwGYDgJ+WZXUQUTcRPTHQh5KbnPPTiQBCiIuIeMnQbIaIwiak22APLFgi4l7btsPT9edZSCDs73zf/xbpcgw5Msvucc6PxwIIIRqMsbWZLbNNuM85P/YPQPhirtH0AGBj2O5m801Xa60VAIwqpRzXdcdjARazoKnXoq7StGjiKchj0O6c5QR+A4FfFTBCmWwyAAAAAElFTkSuQmCC";

	private static  String ABOUT =
					"<hr>" +
									"<p>powered by a simple Java <a href=\"https://github.com/MarvinMenzerath/HTTP-Server\" target=\"_blank\">HTTP-Server</a> created by <a href=\"https://github.com/MarvinMenzerath\" target=\"_blank\">Marvin Menzerath</a></p>";

	private static  String STYLE =
					"html * { font-family: sans-serif;!important }" +
									"body { margin: 1em }" +
									"h1 { font-size: 2em; margin: 0 0 .2em 0 }" +
									"p { font-size: .9em }" +
									"table { border-collapse: collapse; margin-bottom: 1em }" +
									"th { background: #70a0b2; color: #fff }" +
									"td, tbody th { border: 1px solid #e1e1e1; font-size: .85em; padding: .5em .5em }" +
									"tr:hover td { background: #e9edf1 }" +
									"td.center { text-align: center }" +
									"div.directory { background-image: url(" + ICON_DIRECTORY + "); margin: 0 auto; height: 18px; width: 24px }" +
									"div.file { background-image: url(" + ICON_FILE + "); margin: 0 auto; height: 18px; width: 13px }" +
									"div.back { background-image: url(" + ICON_BACK + "); margin: 0 auto; height: 18px; width: 24px }";

	public static String getDirectoryTemplate(String title, String content) {
		return "<!DOCTYPE html>" +
						"<html>" +
						"<head>" +
						"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
						"<link rel=\"icon\" type=\"image/png\" href=\"" + ICON_DIRECTORY_FULL + "\" />" +
						"<style>" + STYLE + "</style>" +
						"<title>" + title + "</title>" +
						"</head>" +
						"<body>" +
						"<h1>" + title + "</h1>" +
						content +
						ABOUT +
						"</body>" +
						"</html>";
	}

	public static String getErrorTemplate(String error) {
		return "<!DOCTYPE html>" +
						"<html>" +
						"<head>" +
						"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
						"<link rel=\"icon\" type=\"image/png\" href=\"" + ICON_WARNING_FULL + "\" />" +
						"<style>" + STYLE + "</style>" +
						"<title>" + error + "</title>" +
						"</head>" +
						"<body>" +
						"<h1>" + error + "</h1>" +
						ABOUT +
						"</body>" +
						"</html>";
	}
}