
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

   	if (result.status == 200 && result.message == "success") {
	$("#incorrectmail").html("Please Check your inbox or Spam for OTP")
	$('#gmailaddress').prop("disabled", true);
	$('#forgotbtndisable').css("display","none")
	$("#otpenable").css("display","block");
	}
	else
	{
		$('#forgotsubmit').prop("disabled", false);
		$('#gmailaddress').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
		}
	})

}else{
	$('#forgotsubmit').prop("disabled", false);
	$('#gmailaddress').prop("disabled", false);
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
	$("#incorrectmail").html("Check your inbox or Spam for OTP");
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
	$("#incorrectmail").html("Please Check your inbox or Spam for OTP")
	$("#registerdetails").append(result.content);  
	$('#registerbtndisable').css("display","none");
	
	}
	else if(result.status == 101 && result.message == "OTP-nonsuccess")
	{
		$('#registerbtn').prop("disabled", false);
		$('#gmail').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid OTP");$("#incorrectmail").css("color","red");
	}
	else if(result.status == 102 && result.message == "exists-nonsuccess")
	{
		$('#gmail').prop("disabled", false);
		$('#registerbtn').prop("disabled", false);
		$("#incorrectmail").html("Gmail already Registered");$("#incorrectmail").css("color","red");
    }
	else
	{
		$('#registerbtn').prop("disabled", false);
		$('#gmail').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
		}
	})

}else{
	$('#registerbtn').prop("disabled", false);
	$('#gmail').prop("disabled", false);
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
	var stateRegister = $("#inputState").val();
	var cityRegister = $("#inputDistrict").val();
	
	
	
	if(hospitalRegister!="" && passwordRegister!="")
	{
	const singupValues=
	{
		hospital:hospitalRegister,
		gmail:registermailSaver,
		password:passwordRegister,
		state:stateRegister,
		city:cityRegister
	}
	
	
	$.post("signup",singupValues,function(registeredResult){
		
		if(registeredResult.status == 200 && registeredResult.message == "success")
		{
			window.location.replace(registeredResult.content);
		}
		else{
			$("#incorrectmail").html("Contact Etiicos Team");
            $("#incorrectmail").css("color","red");	
		}
	});
	
	}
	else
	{
		$("#incorrectmail").html("Enter Hospital & password");
        $("#incorrectmail").css("color","red");	
	}
}
