var databaseID = $("#hospitalid").text();
//console.log($("#hospitalid").text())
$.post("innerprocess/recipientdatas/",{databaseName : databaseID},function(deleteDataResult)
	    {
			    //console.log("Card ID      :"+deleteDataResult.content.callno);	
			   // console.log("Card status  :"+deleteDataResult.content.statusdata);
				//console.log("Card Content :"+deleteDataResult.content.doctordata);
				unAcknowledge();
		});	
		




//Doctor DashBoard Every 15 Sec Auto refresh	 --Start	
	   if(window.location.pathname == "/opdetiicos/doctorDashboard")
     {
	    $("document").ready(function()
       {
	    setInterval(function(){
		unAcknowledge();
		//console.log("Doctor DashBoard :: 15 Sec Auto refresh ");
	},2000);
	 });
	    } 
//Doctor DashBoard Every 15 Sec Auto refresh	 --End



// To Get The Card Call Data  --Start 	
function unAcknowledge()
	{

     $.post("innerprocess/recipientdatas/",{databaseName : databaseID},function(unAcknowledgeResult){
      var pendingdetails="";
      let sno="";
      var callIDArray=[];
      var indexCallID =0;
	 if (unAcknowledgeResult.status == 200 && unAcknowledgeResult.message == "success") {
		
		
		
			$.each(unAcknowledgeResult.content.callno,function(pendingIndex,pendingResult)
		   {
			callIDArray.push(unAcknowledgeResult.content.callno[indexCallID]);
			indexCallID++;
			})

		
		
		
		$.each(unAcknowledgeResult.content.callno,function(pendingIndex,pendingResult)
		{	
			sno=pendingIndex+1;
               
 // check If call is Active or Not --> Start     
             $.each(unAcknowledgeResult.content.callno,function(pendingHospitalIndex,pendingCardIDResult)
           {
           if(pendingIndex == pendingHospitalIndex)
           {
	         pendingdetails+='<div id="divID'+pendingCardIDResult+'" class="col-lg-12" ><div class="card info-card sales-card"><a ><div class="card-body" style="height: 10rem"; ><h5 class="card-title">';	        
            } 
            })
            

           var indexIDCard=0; 
             $.each(unAcknowledgeResult.content.statusdata,function(pendingHospitalIndex,pendingStatusResult)
           {
			   
           if(pendingIndex == pendingHospitalIndex)
           { 
			  
	         if(pendingStatusResult==1){
				 pendingdetails+=' </h5>';	    
	             pendingdetails+='<div class="d-flex align-items-center" style="margin-bottom: 1rem; margin-top: -1rem;"> <div class="card-icon rounded-circle d-flex align-items-center justify-content-center">' ;
	             nurseCallCardActive(callIDArray[indexIDCard]);
	             
			 }else{
				 pendingdetails+=' </h5>';	    
	             pendingdetails+='<div class="d-flex align-items-center" style="margin-bottom: 1rem; margin-top: -1rem;"> <div class="card-icon rounded-circle d-flex align-items-center justify-content-center">' ;
			 }
	        
            }
            indexIDCard++; 
            })               
 // check If call is Active or Not --> End         
              



                
           var indexIDCard2=0;   
           $.each(unAcknowledgeResult.content.doctordata,function(pendingrequestIndex,doctorDataResult)
           {	   
           if(pendingIndex == pendingrequestIndex)
           {
			  pendingdetails+='<i class="bi bi-clipboard2-pulse-fill"></i></div><div class="ps-3"><h6 id="textAreac'+callIDArray[indexIDCard2]+'">'+doctorDataResult+'</h6></div>'; 
           }
            indexIDCard2++;  
           })
           
           
           
           
            $.each(unAcknowledgeResult.content.callno,function(pendingrequestIndex,pendingrequestResult)
           {   
           if(pendingIndex == pendingrequestIndex)
           {
	         pendingdetails+='</div><button id="btnIDStart'+pendingrequestResult+'" onclick="nurseCallCard(this)"  value="'+pendingrequestResult+'"  type="button" class="btn btn-primary btn-lg sales-card" style="margin:0px 0px 0px 43px;"><i class="fa-solid fa-user-nurse" ></i> Call</button> ';
               pendingdetails+='<button id="btnIDEnd'+pendingrequestResult+'" onclick="nurseCallCardEnd(this)"  value="'+pendingrequestResult+'"  type="button" class="btn btn-danger btn-lg sales-card" style="margin:0% auto auto 20%;"><i class="fa-solid fa-user-nurse" ></i> End</button>  </div></a>';
           } 
           })
        
               
            pendingdetails+="</div>";
      	})
      	$("#callCardIdAdd").html(pendingdetails);
      	
           }
           
            var indexIDCard1=0; 
             $.each(unAcknowledgeResult.content.statusdata,function(pendingHospitalIndex,pendingStatusResult)
           {

	         if(pendingStatusResult==1){
				  var textAreaDataIDs ="#btnIDStart"+callIDArray[indexIDCard1];	
	             $(textAreaDataIDs).prop("disabled", true);
	             
	              var textAreaDataIDs ="#btnIDStart"+callIDArray[indexIDCard1];	
               $(textAreaDataIDs).css("background-color", "#28A745"); 
	          //   console.log(callIDArray[indexIDCard1]);
			 }else{
				  var textAreaDataIDs ="#btnIDEnd"+callIDArray[indexIDCard1];	
	             $(textAreaDataIDs).prop("disabled", true);
			 }
	        
            
            indexIDCard1++; 
            })      
           
           
		     });
		       } 
// To Get The Card Call Data  --End 			       
		       




 //-------------To get Call Start Time ------------->Start
  var startCallTimeVar="";
   var startCallDateVar="";
     var startCallDateVarCall="";
  
  function getStartTime(){
	  
	  var startCallDate=new Date().toLocaleDateString(); 
	  var startCallTime=  new Date().toLocaleTimeString(); 
	  startCallTimeVar=startCallTime;
	  startCallDateVar=startCallDate;
	  
	  var currentTime = new Date();
	  var day = currentTime.getDate();
	  var year = currentTime.getFullYear();
	  var getMonth =['Jan','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	  var getMonthVar = getMonth[currentTime.getMonth()];
	  
	  var startDate = day+" "+getMonthVar+" "+year;
	 startCallDateVarCall= startDate;
	 // console.log("Start Time :: "+startCallDateVarCall+" "+startCallTimeVar);	
  }
  
   //-------------To get Call Start Time ------------->End
  
  
  
  
  
  
  

  


// nurse Call Data Value ofCard Actve  (START Call Button)--> Start
function nurseCallCardActive(NurseCallCard){
	
	var data = NurseCallCard;	
    var infoButtongreen = '<button id="'+data+'infoAckRed" type="button" class="btn btn-primary" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';
	var infoButtonred = '<button id="'+data+'infoAckRed" type="button" class="btn btn-primary" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';


	var database = $("#hospitalid").text();
	var infoCardGreen = '#infoCardGreen'+data; 
	var infoCardRed = '#infoCardRed'+data; 
	



	
			var ActiveData={
	     	dataBaseName: database,
			tableName:data
			 }


//console.log(startCallTimeVar);
	//	var callData={
	  //   	dataBaseName: database,
		//	status:true,
	//		callno:data,
//			time:startCallTimeVar,
//			duration:" ",
//			endView:"Doctor"
//			 }

	
//		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResult)
//	    {
//		})
	

	$.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	    {
   // console.log("TRUE :: "+activeDataResult.content);
	if(activeDataResult.content==true){
		
		if(infoButtongreen==$(infoCardGreen).html()){
		      //   $(infoCardGreen).remove();
			   //  $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtonred==$(infoCardGreen).html()){
		       //  $(infoCardGreen).remove();
			   //  $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtongreen!=$(infoCardGreen).html()){
			   //   $(infoCardGreen).append(infoButtongreen);
		}
		
		
var intervalFiveSec="";			
var cardIDRefresh ="#"+data;	
//console.log("Auto refesh 5 Seconds."+cardIDRefresh);
             
			if(activeDataResult.content==true)
               {
                 $(cardIDRefresh).ready(function()
                      {
	                intervalFiveSec=  setInterval(function(){
		                                  postDataCard(cardIDRefresh);
		                                 //  console.log("START");
		                                  },5000);
	                  });
                }



function postDataCard(dataID)
{
	        $.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	                 {	
					//	console.log("FALSE :: "+activeDataResult.content);	
	                    if(activeDataResult.content==false){ 		
		                var infoButtonRed =dataID+"infoAckRed";
		             //   $(infoButtonRed).css('background','#28A745');
		                clearInterval(intervalFiveSec);
		              //   console.log("STOP");
	                                                        }
	                                                        else{
															//	 $(infoButtonRed).css('background','#28A745');
															}
	                 })
};
  		
}else{		
		 console.log($(infoCardGreen).html());
	}
		
  })
}
// nurse Call Data Value ofCard Actve (START Call Button) --> End	       
		       
		       
		       
		
		
		       
	// nurse Call Data Value ofCard  (START Call Button) --> Start

function nurseCallCard(NurseCallCard){
	getStartTime();
	
	 data = NurseCallCard.getAttribute("value");	
    var infoButtongreen = '<button id="'+data+'infoAckRed" type="button" class="btn btn-primary" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';
	var infoButtonred = '<button id="'+data+'infoAckRed" type="button" class="btn btn-primary" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';

   
	var database = $("#hospitalid").text();
	var infoCardGreen = '#infoCardGreen'+data; 
	var infoCardRed = '#infoCardRed'+data; 
  
			var ActiveData={
	     	dataBaseName: database,
			tableName:data
			 }
			 
	     var textAreaDataID ="#textAreac"+data;	
	     var typedata = $(textAreaDataID).text();
	   	     	
	   	     //	console.log(typedata)
	  var textAreaDataIDs ="#btnIDStart"+data;	
	  
	  $(textAreaDataIDs).prop("disabled", true);
	  
	  var textAreaDataIDEnd ="#btnIDEnd"+data;	
	   $(textAreaDataIDEnd).prop("disabled", false);  
	   $(textAreaDataIDs).css("background-color", "#28A745");  
	  
	  
	   	     	// console.log(textAreaDataIDs)
  var startTimeDate = startCallDateVarCall+" "+startCallTimeVar;			 
		var callData={
	     	dataBaseName: database,
			status:true,
			dataTyped:typedata,
			callno:data,
			startTime:startTimeDate,
			endTime:" ",
			endView:"Doctor"
			 }

	
		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResult)
	    {
		})
	

	$.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	    {
  //  console.log("TRUE :: "+activeDataResult.content);
//	if(activeDataResult.content==true){
		
		if(infoButtongreen==$(infoCardGreen).html()){
		       //  $(infoCardGreen).remove();
			   //  $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtonred==$(infoCardGreen).html()){
		       //  $(infoCardGreen).remove();
			    // $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtongreen!=$(infoCardGreen).html()){
			    //  $(infoCardGreen).append(infoButtongreen);
		}
		
		
var intrevel5secload="";			
var cardIDRefresh ="#"+data;	
//console.log("Auto refesh 5 Seconds."+cardIDRefresh);
             
			if(activeDataResult.content==true)
               {
                 $(cardIDRefresh).ready(function()
                      {
	                 intrevel5secload= setInterval(function(){
		                                  postDataCard(cardIDRefresh);
		                                  // console.log("START");
		                                  },5000);
	                  });
                }



function postDataCard(dataID)
{
	        $.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	                 {	
						//console.log("FALSE :: "+activeDataResult.content);	
	                    if(activeDataResult.content==false){ 		
		                var infoButtonRed =dataID+"infoAckRed";
		               // $(infoButtonRed).css('background','#28A745');
		                clearInterval(intrevel5secload);
		              //  console.log("STOP");
	                                                        }
	                                                        else{
																// $(infoButtonRed).css('background','#28A745');
															}
	                 })
};
  		
//}else{		
	//	 console.log($(infoCardGreen).html());
//	}
		
  })
}


// nurse Call Data Value ofCard  (START Call Button) --> End











//--------------acknowledge  Call End Button (END Call Button) --> Doctor ------------- Start
  var endDateVar="";
  var endCallTimeVar= "";
  var endDateCallVar= "";

	var data="";
	function nurseCallCardEnd(ackData){
		  data = ackData.getAttribute("value");
		  
		  
	  var textAreaDataIDs ="#btnIDStart"+data;	
	             $(textAreaDataIDs).prop("disabled", false);	  
		  

 var textAreaDataIDEnd ="#btnIDEnd"+data;	
	   $(textAreaDataIDEnd).prop("disabled", true); 
var textAreaDataIDs ="#btnIDStart"+data;
$(textAreaDataIDs).css("background-color", "#0D6EFD");
  var database = $("#hospitalid").text();
  
  var endDate=new Date().toLocaleDateString(); 
  var endCallTime=  new Date().toLocaleTimeString();
  endDateVar=endDate;
  endCallTimeVar=endCallTime;
   
   
   
     var currentTime = new Date();
	  var day = currentTime.getDate();
	  var year = currentTime.getFullYear();
	  var getMonth =['Jan','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	  var getMonthVar = getMonth[currentTime.getMonth()];
	  
	  var endDateCall = day+" "+getMonthVar+" "+year;
	 endDateCallVar= endDateCall;
		
   
   
   
   var textAreaDataIDs ="#btnIDEnd"+data;	
	   	     	// console.log(textAreaDataIDs)
   
 // console.log("EndTime :: "+endDateCallVar+" "+endCallTimeVar);

var endDateTime= endDateCallVar+" "+endCallTimeVar;

 var textAreaDataID ="#textAreac"+data;	
 var typedata = $(textAreaDataID).text();


   	var callData={
	     	dataBaseName: database,
			status:false,
			callno:data,
			datatyped:" ",
			startTime:" ",
			endTime:endDateTime,
			endView:"Doctor"
			 }

	
		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResult)
	    {
		}) 

	}	       
		    

//----------acknowledge  Call End Button  (END Call Button)  --> Doctor  ----------------- End