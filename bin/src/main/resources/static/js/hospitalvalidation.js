var forgotmailSaver;
$("#forgotsubmit").on("click",function(){
	$('#forgotsubmit').prop("disabled", true);
	$("#gmailaddress").prop("disabled",true);
	$("#incorrectmail").html("Please wait to send OTP");
	$("#incorrectmail").css("color","green");
	
var forgotmail	= $("#gmailaddress").val();
forgotmailSaver = forgotmail;
var forgotFormate=/^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
if(forgotFormate.test(forgotmail) && forgotmail!="")
{
	 $.post("innerprocess/doctorOTP/",{"gmail":forgotmail},function(result){
		 console.log(result)
   	if (result.status == 200 && result.message == "success") {
		   
	$("#gmailaddress").prop("disabled",true);
	$("#hideSubmit").css("display","none");
	console.log(result.content)
	$("#doctorexists").html(result.content);
	
	}
	else
	{
	$('#forgotsubmit').prop("disabled", false);
	$("#gmailaddress").prop("disabled",false);
	$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
	}
	})

}else{
	$('#forgotsubmit').prop("disabled", false);
	$("#incorrectmail").html("Enter Valid Gmail ID");
    $("#incorrectmail").css("color","red");	
}
});

function doctorloginbtn(){
	
	      var pin=$("#pin").val();
	      if (pin > 31 && (pin < 48 || pin > 57))
	      {
	
   var recipientID=$("#recipientid").val()
	
	if(recipientID!="" && pin!="")
	{
	var doctorLoginData={
		  
		   pin:$("#pin").val(),
	       recipientID:$("#recipientid").val(),
	       doctorGmail:forgotmailSaver
	}
	
	
	
	$.post("innerprocess/doctorDashboard/",doctorLoginData,function(doctorDataResult){
	
	if(doctorDataResult.status == 200 && doctorDataResult.message == "success")
	{
		
	    window.location.replace(doctorDataResult.content)
	}	
	else
	{
		$("#invalidOTP").html("Please Verifiy OTP and Recipient Name ID");
        $("#invalidOTP").css("color","red");
	}
	
		})
		}
		else
		{
			$("#invalidOTP").html("Please Verifiy OTP and Recipient Name ID");
            $("#invalidOTP").css("color","red");	
		}
	}
	else
	{
		$("#invalidOTP").html("Please Enter Number");
        $("#invalidOTP").css("color","red");
	}
};




function existsdoctorloginbtn(){
	console.log("dfds doct")
          var pin=$("#pin").val();
	      if (pin > 31 && (pin < 48 || pin > 57))
	      {
			  var existsdoctorLoginData={
		  
		   otp:$("#pin").val(),
	      doctorGmail:forgotmailSaver
	}
	
	console.log(existsdoctorLoginData)
	
	$.post("innerprocess/exists/DoctorDashboard/",existsdoctorLoginData,function(existsDoctorDataResult){
	
	console.log(existsDoctorDataResult)
	
	if(existsDoctorDataResult.status == 200 && existsDoctorDataResult.message == "success")
	{
		window.location.replace(existsDoctorDataResult.content)
		
		}
	else
	{
		$("#invalidOTP").html("Please Verifiy OTP");
        $("#invalidOTP").css("color","red");
	}
		
		});
			  
			  		  
			  }

};


