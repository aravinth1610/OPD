


$("#unackbtn").on("click",function()
{
	var doctorDashboard="/opdetiicos/home";
	window.location.replace(doctorDashboard);
})



$("#recipientlogbtn").on("click",function()
{
	var doctorlog="/opdetiicos/log";
	window.location.replace(doctorlog);
})


//-----------to pass Database name --> START
var v = $("#hospitalid").text();

//-----------to pass Database name --> END		


var audio = new Audio("assets/AtteanceRequired.wav");


//-----------Reload Page Every 5 SEC --> START
	   if(window.location.pathname == "/opdetiicos/home")
     {
	 $( "#clickme" ).click();
	 
	 $("#aggree").on("click",function(){
 audio.play();
 
 })
 
 	 $("#disaggree").on("click",function(){
 audio.pause();
 
 })
 
 
 
	 unAcknowledge();

	    $("document").ready(function()
       {
	    setInterval(function(){
		unAcknowledge();
	},5000);
	 });
	    }
//-----------Reload Page Every 5 SEC --> START






//-----------GET DATA IF DOCTOR CALL ACTIVE ONLY--> START	
function unAcknowledge()
	{
	
    $.post("innerprocess/recipientdatas/",{databaseName : v},function(unAcknowledgeResult){
	var pendingdetails="";
    let sno="";
 
	 if (unAcknowledgeResult.status == 200 && unAcknowledgeResult.message == "success") {
		
   	$.each(unAcknowledgeResult.content.statusdata,function(pendingIndex,pendingResult)
		{	
		sno=pendingIndex+1;
		if(pendingResult==1){
           
           
           $.each(unAcknowledgeResult.content.callno,function(pendingHospitalIndex,pendingHospitalCallNo)
           {
           if(pendingIndex == pendingHospitalIndex)
           {
	         pendingdetails+='<div id="'+pendingHospitalCallNo+'contentDiv" class="col-xxl-8 col-xl-12" ><div class="card info-card sales-card"> <div class="card-body"><h5 class="card-title">';
           
            } 
            })
            
            
             $.each(unAcknowledgeResult.content.doctorname,function(doctorNamePending,doctorName)
           {
           if(pendingIndex == doctorNamePending)
           {
	         pendingdetails+=''+doctorName +'';

            } 
            })

               $.each(unAcknowledgeResult.content.wardname,function(doctorNameWardPending,doctorWard)
           {
           if(pendingIndex == doctorNameWardPending)
           {
	         pendingdetails+='<a style="padding-left:37px;"> Room No: '+doctorWard+'</a></h5><div class="d-flex align-items-center"> <div class="card-icon rounded-circle d-flex align-items-center justify-content-center"><i class="bi bi-clipboard2-pulse-fill"></i>  </div><div class="ps-3"></div>';
           
            } 
            })
            
            
                 $.each(unAcknowledgeResult.content.callno,function(pendingHospitalIndex,pendingHospitalCallNo)
           {
           if(pendingIndex == pendingHospitalIndex)
           {
	          pendingdetails+='<h6 id="textAreac'+pendingHospitalCallNo+'">';
           
            } 
            })
            
            
            
            
            
              

           $.each(unAcknowledgeResult.content.doctordata,function(pendingrequestIndex,pendingrequestDoctorData)
           { 
           if(pendingIndex == pendingrequestIndex)
           {
			  pendingdetails+=' '+pendingrequestDoctorData+'</h6></div></div> </div></div> '; 
           } 
           })
           
           
            $.each(unAcknowledgeResult.content.callno,function(pendingrequestIndex,pendingrequestCallNo)
           { 
           if(pendingIndex == pendingrequestIndex)
           {
	         pendingdetails+='<div id="'+pendingrequestCallNo+'ackDiv" class="col-xxl-4 col-xl-12" > <div  value="'+pendingrequestCallNo+'" onclick="acknowledgeHideFunction(this)"  ><div class="card info-card customers-card " id="endCard" style="height:155px;"> <div class="card-body" > <h5 class="card-title"></h5><div class="d-flex align-items-center"><div class="card-icon rounded-circle d-flex align-items-center justify-content-center"><i class="bi bi-x-circle"></i></div> ';
             pendingdetails+='<div class="ps-3"> <h6 class="endCallText" id="endCard">END Call</h6></div> </div> </div></div> </div>';
          
           } 
           })
        
               
            pendingdetails+="</div>";
            
            }
      	})
      	$("#nurseack1").html(pendingdetails);
      	
      	
      	var status = unAcknowledgeResult.content.statusdata;
      	var audioPlayPause = unAcknowledgeResult.content.audiostatus;
      	var cardId = unAcknowledgeResult.content.callno;
    
      		
for(var i = 0 ;i<status.length;i++)
   {
	  if(status[i] == 1 && audioPlayPause[i] === 'play')
	     {
		  audioPlay(cardId[i]);
		  audioPause();
		 }
	}
	
	
	       }
           else{} //$("#scrolldisplayreckcalls").css("display","block")
		     });
		       } 
		       
//-----------GET DATA IF DOCTOR CALL ACTIVE ONLY--> END			       
	
	
function audioPause()
{

audio.pause();	
}
	
	       
function audioPlay(cardID)
{
setTimeout(function(){
	audio.play();
    var database = $("#hospitalid").text(); 
  	var callData={
	     	dataBaseName: database,
			tableName:cardID,
			 }
		$.post("innerprocess/recipientaudiopause/",callData,function(callDataResultEnd)
	    {
		})   
},2000)

}


		       
//  acknowledge Div CAll BUTTON  ---> Start
	
var endDateVar="";
var endCallTimeVar= "";
var endDateCallVar= "";
var data="";
	
	
function acknowledgeHideFunction(ackData){

	data = ackData.getAttribute("value");
   var contentDiv = document.getElementById(data+"contentDiv");
    var ackDiv = document.getElementById(data+"ackDiv");
    contentDiv.style.display = "none";  
    ackDiv.style.display = "none"; 

    var database = $("#hospitalid").text();
    
  var endCallTime= new Date(); 
	  
	  
  var hours = endCallTime.getHours();
  var minutes = endCallTime.getMinutes();
  var seconds = endCallTime.getSeconds();
  var millisecond = endCallTime.getMilliseconds();
  var ampm = hours >= 12 ? 'PM' : 'AM';
  hours = hours % 12;
  hours = hours ? hours : 12; // the hour '0' should be '12'
  minutes = minutes < 10 ? '0'+minutes : minutes;
  var endTime = hours+':'+minutes+':'+seconds+':'+millisecond+' '+ ampm;
  endCallTimeVar=endTime
  
  
  
  
  
  
      var currentDate = new Date();
	  var day = currentDate.getDate();
	  var year = currentDate.getFullYear();
	  var getMonth =['Jan','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	  var getMonthVar = getMonth[currentDate.getMonth()];
	  
	  var endDateCall = day+" "+getMonthVar+" "+year;
	 endDateCallVar= endDateCall;
 
//-------------------- AM  or PM   
   
   
   
    
 var endDateTime= endDateCallVar+" "+endCallTimeVar; 
 var textAreaDataID ="#textAreac"+data;	
 var typedata = $(textAreaDataID).text();		 
 	var callData={
	     	dataBaseName: database,
			status:false,
			callno:data,
			datatype:typedata,
			startTime:"",
			endTime:endDateTime,
			endView:"Nurse",
			doctorName:""
			 }
		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResultEnd)
	    {
		//if(callDataResultEnd.status == 200 && callDataResultEnd.message == "success"){}
		//else{} //$("#scrolldisplaycallEnder").css("display","block")
		
		}) 
    
	}	       
		       
//  acknowledge Div CAll BUTTON  ---> END     