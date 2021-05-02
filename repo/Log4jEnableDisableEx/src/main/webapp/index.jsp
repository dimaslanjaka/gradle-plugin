<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	    <title>Log4j</title>
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	    <script type="text/javascript">
	        $(document).ready(function() {	            
	        	// Selecting One Checkbox At A Time
	            $(':checkbox').on('change', function() {
	                var th = $(this),
	                    name = th.prop('name');
	                if (th.is(':checked')) {
	                    $(':checkbox[name="' + name + '"]').not($(this)).prop('checked', false);
	                }
	            });
	        	
	            $('#loggingEnableDisable').on('submit', function(event) {
	            	var isChecked = false;
	            	var checkbox = document.getElementsByName("log4jMode");
	                for (var i = 0; i < checkbox.length; i++) {
	                    if (checkbox[i].checked) {
	                        isChecked = true;
	                        break;
	                    }
	                }
	                if (isChecked) {              
	                    $('#loggingEnableDisable').submit();
	                } else {
	                	document.getElementById('error').innerHTML="Please select a checkbox .....!";
	                	event.preventDefault();
	                }
	            });
	        });
	    </script>
	</head>
	<body>
	    <h1>Enable Or Disable Log4j</h1>
	    <form id="loggingEnableDisable" action="loggingServlet" method="post" enctype="application/x-www-form-urlencoded">
	        <!--Debug Mode -->
	        <input type="checkbox" id="ex_check1" name="log4jMode" value="DEBUG" /><span id="debugId">'Debug' Mode</span>
	
	        <!-- Info Mode -->
	        <input type="checkbox" id="ex_check2" name="log4jMode" value="INFO" /><span id="infoId">'Info' Mode</span>
	
	        <!-- Warn Mode -->
	        <input type="checkbox" id="ex_check3" name="log4jMode" value="WARN" /><span id="warnId">'Warn' Mode</span>
	
	        <!-- Error Mode -->
	        <input type="checkbox" id="ex_check4" name="log4jMode" value="ERROR" /><span id="errorId">'Error' Mode</span>
	
	        <!-- Off -->
	        <input type="checkbox" id="ex_check5" name="log4jMode" value="OFF" /><span id="errorId">Off</span>
	
	        <input type="submit" id="submitBtn" value="Send" />
	    </form>
	    <div id="error" style="color: red; padding: 12px 0px 0px 23px;"></div>
	</body>
</html>