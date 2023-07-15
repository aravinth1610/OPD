
var perDoctorPrice;

$.get("innerprocess/etiicos/perdoctoramount/",function(perDoctorAmountResult){
	
	if(perDoctorAmountResult.status == 200 && perDoctorAmountResult.message == 'success')
	{
		$("#perprice").html(perDoctorAmountResult.content);
		perDoctorPrice=perDoctorAmountResult.content;
	}
})

function perDoctorCount()
{
		var currentDate = new Date();
		
		var month = currentDate.getMonth()+1;
		var date =  currentDate.getDate();
		var year =  currentDate.getFullYear();
		
		let perDayCost;
		
		var currentMonthDays = new Date(year, month, 0).getDate();
		
		if(currentMonthDays == "31")
		{
		var perDayDoctorPrice = perDoctorPrice / 31;
		perDayCost = perDayDoctorPrice.toFixed(2);
		}
		else if(currentMonthDays == "30")
		{
		var perDayDoctorPrice = perDoctorPrice / 30;
	    perDayCost = perDayDoctorPrice.toFixed(2);
		}
		else if(currentMonthDays == "28")
		{
		var perDayDoctorPrice = perDoctorPrice / 28;
	    perDayCost = perDayDoctorPrice.toFixed(2);
	    }
		else
		{
		var perDayDoctorPrice = perDoctorPrice / 29;
	    perDayCost = perDayDoctorPrice.toFixed(2);
		}
			
		var perDoctorCount = $("#percount").val();
		var perDayCount;
			
			if(date=='1')
			{
				perDayCount = currentMonthDays;
			}else
			{
				perDayCount = currentMonthDays -  date;
			}
		let perDoctorDayCount = (perDayCount * perDayCost) * perDoctorCount;
		
		let PriceValue =(perDoctorDayCount * 100)/100;
		
		let overAllPrice = PriceValue.toFixed(2);

		let GSTValue= (overAllPrice / 100) * 18;
		
		let overAllValue = PriceValue + GSTValue;
			
		const fixedValue = 	overAllValue.toFixed(2);
		
		let payAmountValue;
			
		if(fixedValue >= 1)
		{
			payAmountValue = fixedValue;
		}
		else
		{
		     payAmountValue = 1;
		}
			
		$("#countvalue").html(payAmountValue);
		
		
	}
	
$("#mailconfirm").on('click',function(){
	
	$("#incorrectmail").html("Please wait to send OTP");
	$("#incorrectmail").css("color","green");	
	$('#mailconfirm').prop("disabled", true);
	$('#gmail').prop("disabled", true);
	
	var paymentGmail =  $("#gmail").val();
	
	var gmailFormate=/^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;

      if(gmailFormate.test(paymentGmail) && paymentGmail!="")
       {
	
	$.post("innerprocess/etiicos/paymentgmailverify/",{gmail:paymentGmail},function(paymentVerifyResult){
		
		
  if (paymentVerifyResult.status == 200 && paymentVerifyResult.message == "success") {
	
	$("#incorrectmail").html("Please Check your inbox or Spam for OTP")
	$('#gmail').prop("disabled", true);
	$('#gmailconfirm').css("display","none")
	$("#otpverifytag").html(paymentVerifyResult.content);
	}
	else if (paymentVerifyResult.status == 101 && paymentVerifyResult.message == "payment-OTP-nonsuccess") {
	
	$("#incorrectmail").html("Please Check your Internet Connection")
	$('#gmail').prop("disabled", false);
	$('#mailconfirm').prop("disabled", false);
		}
	else if (paymentVerifyResult.status == 102 && paymentVerifyResult.message == "payment-exists-nonsuccess") {
	
	$("#incorrectmail").html("Gmail ID is not Register")
	$('#gmail').prop("disabled", false);
	$('#mailconfirm').prop("disabled", false);
		}
	else
	{
		$('#mailconfirm').prop("disabled", false);
		$('#gmail').prop("disabled", false);
		$("#incorrectmail").html("Enter Valid Gmail ID");$("#incorrectmail").css("color","red");
		}
		
	})
	}
	else{
	$('#gmail').prop("disabled", false);
	$('#mailconfirm').prop("disabled", false);
	$("#incorrectmail").html("Enter Valid Gmail ID");
    $("#incorrectmail").css("color","red");	
}
});	



function verifypayment()
{
	
	$("#incorrectmail").html(" ");
	$("#otpbutton").prop("disabled",true);
	
	var paymentRegisterGmail = $("#gmail").val();
	var paymentRegisterOTP = $("#otppyamentregister").val();
	
	var paymentOTPVerify ={
		
		gmail:paymentRegisterGmail,
		otp:paymentRegisterOTP
	}
	
    if (paymentRegisterOTP > 31 && (paymentRegisterOTP < 48 || paymentRegisterOTP > 57))
	{
	
	$.post("innerprocess/etiicos/paymentgOTPverify/",paymentOTPVerify,function(paymentOTPVerifiedResult){
		
		
		if(paymentOTPVerifiedResult.status == 200 && paymentOTPVerifiedResult.message == "success")
		{
			$("#otpverifytag").css("display","none");
			$("#paymentContent").html(paymentOTPVerifiedResult.content);
			
			}
		else if(paymentOTPVerifiedResult.status == 400 && paymentOTPVerifiedResult.message == "not-validOTP-success")
		{
			$("#incorrectmail").html("Please Enter Valid OTP");
			$("#incorrectmail").css("color","indianred");
			$("#otpbutton").prop("disabled",false);
		}
		else
		{
			$("#incorrectmail").html("Please Contact Etiicos Team");
			$("#incorrectmail").css("color","indianred");
			$("#otpbutton").prop("disabled",false);
		}
	})
	}
	else
	{
		    $("#incorrectmail").html("Please Enter Number");
			$("#incorrectmail").css("color","indianred");
			$("#otpbutton").prop("disabled",false);
	}
}

function paymentconfrim()
{
	var payAmountValue = $("#countvalue").html();
	var paymentGmail = $("#gmail").val();
	var perDoctorCount = $("#percount").val();
		
	$.post("innerprocess/etiicos/pay/",{amount:payAmountValue},function(paymentResultDetails){
		
		if(paymentResultDetails.status == 200 && paymentResultDetails.message == 'success')
		{
			openPaymentCheckout(paymentResultDetails.content,paymentGmail,perDoctorCount)
		}
		
	})
	
	
}

function openPaymentCheckout(paymentDetails,gmail,perDoctorCount)
{
	var options = {
			
			order_id:paymentDetails.orderid,
			key:paymentDetails.keyid,
		    amount:paymentDetails.amount,
			currency:paymentDetails.currency,
			name:"Etiicos",
			description:"Test Transaction",
			image:"assets/img/etiicos-logo.png",
			callback:"http://localhost:8082/opdetiicos/login",
			handler:function (paymentSuccess)
			{
				var paymentSuccessDetails = 
				{
					gmail:gmail,
					orderID:paymentSuccess.razorpay_order_id,
					paymentID:paymentSuccess.razorpay_payment_id,
					signature:paymentSuccess.razorpay_signature,
					count:perDoctorCount
				}
				
				
				$.post("innerprocess/etiicos/paymentsuccess/",paymentSuccessDetails,function(paymentSuccessResult){
					
					
					if(paymentSuccessResult.status == 200 && paymentSuccessResult.message == 'success')
					{
						window.location.replace(paymentSuccessResult.content);
					}
				})
				
				
				
			},
			prefill:{
				"name":'',
				"email":gmail,
				"contact":''
				
			},
			notes:{
				"address":"OPD and Token Payment"
			},
			theme:{
				"color":"white",
				"background-color":"mediumseagreen"
			}		
	}
	
	var rzp1 = new Razorpay(options);
	rzp1.on('payment.failed', function (response){
    alert(response.error.code);
    alert(response.error.description);
    alert(response.error.source);
    alert(response.error.step);
    alert(response.error.reason);
    alert(response.error.metadata.order_id);
    alert(response.error.metadata.payment_id);
})
	
	rzp1.open();
	}

	
	
	