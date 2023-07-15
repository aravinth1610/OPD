$("#registerform").on("click",function(){
	const signupform="/opdetiicos/register";
	window.location.replace(signupform)
});

$("#signinform").on("click",function(){
	const loginform="/opdetiicos/login";
	window.location.replace(loginform)
});

$("#forgotpasswordform").on("click",function(){
	const forgotpasswordform="/opdetiicos/forgotPassword";
	window.location.replace(forgotpasswordform)
});

var forgotmailSaver;
$("#forgotsubmit").on("click",function(){
	$("#incorrectmail").html("Please wait to send OTP");
	$("#incorrectmail").css("color","green");	
	$('#forgotsubmit').prop("disabled", true);

var forgotmail	= $("#gmailaddress").val();
forgotmailSaver = forgotmail;
var forgotFormate=/^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;

if(forgotFormate.test(forgotmail) && forgotmail!="")
{
	 $.post("innerprocess/authorizedOTP/",{"gmail":forgotmail},function(result){
//		console.log(result)
   	if (result.status == 200 && result.message == "success") {
	$("#otpenable").css("display","block");
	}
	else
	{
		$('#forgotsubmit').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
		}
	})

}else{
	$('#forgotsubmit').prop("disabled", false);
	$("#incorrectmail").html("Enter Valid Gmail ID");
    $("#incorrectmail").css("color","red");	
}
});

$("#forgotupdatebtn").on("click",function(){
	
	       var forgotpassword=$("#forgotpassword").val();
	       var pin=$("#pin").val();
	if (pin > 31 && (pin < 48 || pin > 57))
	  {
	if(forgotpassword!="" && pin!="")
	{
	var forgotPasswordData={
	     	gmailaddress: forgotmailSaver,
			forgotpassword:$("#forgotpassword").val(),
	        pin:$("#pin").val()
	}
	

	$.post("innerprocess/forgot/",forgotPasswordData,function(forgotPasswordResult){
		
		if(forgotPasswordResult.status == 200 && forgotPasswordResult.message == 'success')
		{
			window.location.replace(forgotPasswordResult.content);
		}
		else
		{
			$("#invalidOTP").html("OTP is not regnosied");
            $("#invalidOTP").css("color","red");
		}
		
	})
	}
	else
	{
		$("#invalidOTP").html("OTP is not regnosied");
        $("#invalidOTP").css("color","red");	
	}
	
	}
	else
	{
		$("#invalidOTP").html("Please Enter Number");
        $("#invalidOTP").css("color","red");	
	}
});
//

var registermailSaver;
$("#registerbtn").on("click",function(){
	$("#incorrectmail").html("Please wait to send OTP");
	$("#incorrectmail").css("color","green");	

var registermail = $("#gmail").val();
registermailSaver = registermail;
var registerFormate=/^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
$('#gmail').prop("disabled", true);
$('#registerbtn').prop("disabled", true);
if(registerFormate.test(registermail) && registermail!="")
{
	 $.post("innerprocess/etiicos/gmailverify/",{"gmail":registermail},function(result){
			
  	if (result.status == 200 && result.message == "success") 
  	{
	$("#incorrectmail").html("")
	$("#registerdetails").append(result.content);  
	
	}
	else if(result.status == 101 && result.message == "OTP-nonsuccess")
	{
		$('#registerbtn').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid OTP");$("#incorrectmail").css("color","red");
	}
	else if(result.status == 102 && result.message == "exists-nonsuccess")
	{
		$('#gmail').prop("disabled", false);
		$('#registerbtn').prop("disabled", false);
		$("#incorrectmail").html("Gmail is alread Registered");$("#incorrectmail").css("color","red");
    }
	else
	{
		$('#registerbtn').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
		}
	})

}else{
	$('#registerbtn').prop("disabled", false);
	$("#incorrectmail").html("Enter Valid Gmail ID");
    $("#incorrectmail").css("color","red");	
}
});


function validateSignup()
{
	
	var pin = $("#pin").val()
	
	if(pin!="")
	{
	
	var registerValidate = 
	{
		otp:pin,
		registerGmail:registermailSaver
	}
		
		$.post("innerprocess/etiicos/OTPverify/",registerValidate,function(signupValidationResult){
		
		if(signupValidationResult.status ==200 && signupValidationResult.message == "success")
		{
			$("#incorrectmail").html("");
			$("#registerdetails").css("display","none");
			$("#registerbtn").css("display","none");
			$("#registercontent").append(signupValidationResult.content);  
		}
		else
		{
		$("#incorrectmail").html("Enter Valid OTP");
        $("#incorrectmail").css("color","red");	
		}
		});
	}
	else
	{
		
		$("#incorrectmail").html("Enter OTP");
        $("#incorrectmail").css("color","red");	
	}
	
		
	
}

function togglePasword()
{
	
    $("#togglepassword").toggleClass("fa-eye fa-eye-slash");
	  var input = $($("#togglepassword").attr("toggle"));
	  if (input.attr("type") == "password") {
	    input.attr("type", "text");
	  } else {
	    input.attr("type", "password");
	  }
}


function signupsubmit()
{
	var hospitalRegister = $("#hospital").val();
	var passwordRegister = $("#password-field").val();
	
	if(hospitalRegister!="" && passwordRegister!="")
	{
	const singupValues=
	{
		hospital:hospitalRegister,
		gmail:registermailSaver,
		password:passwordRegister
	}
	
	$.post("signup",singupValues,function(registeredResult){
		
		if(registeredResult.status == 200 && registeredResult.message == "success")
		{
			window.location.replace(registeredResult.content);
		}
	});
	
	}
	else
	{
		$("#incorrectmail").html("Enter Hospital & password");
        $("#incorrectmail").css("color","red");	
	}
}
