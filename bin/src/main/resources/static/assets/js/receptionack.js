$("#unackbtn").on("click",function()
{
	var doctorDashboard="/opdetiicos/home";
	window.location.replace(doctorDashboard);
})



$("#recipientlogbtn").on("click",function()
{
	var doctorDashboard="/opdetiicos/log";
	window.location.replace(doctorDashboard);
})


//-----------to pass Database name --> START
var v = $("#hospitalid").text();
//console.log($("#hospitalid").text())
$.post("innerprocess/recipientdatas/",{databaseName : v},function(deleteDataResult)
	    {
			
			  //console.log(".."+deleteDataResult.content.callno);	
			  //console.log(",,,"+deleteDataResult.content.doctordata);
				unAcknowledge();
		});	
//-----------to pass Database name --> END		





//-----------Reload Page Every 5 SEC --> START
	   if(window.location.pathname == "/opdetiicos/home")
     {
	    $("document").ready(function()
       {
	    setInterval(function(){
		unAcknowledge();
		//console.log("Doc DashBoard");
	},5000);
	 });
	    }
//-----------Reload Page Every 5 SEC --> START






//-----------GET DATA IF DOCTOR CALL ACTIVE ONLY--> START	
function unAcknowledge()
	{
	
    $.post("innerprocess/recipientdatas/",{databaseName : v},function(unAcknowledgeResult){
	//console.log(unAcknowledgeResult.content.callno)
    var pendingdetails="";
    let sno="";
 
	 if (unAcknowledgeResult.status == 200 && unAcknowledgeResult.message == "success") {
		
		
		
		$.each(unAcknowledgeResult.content.statusdata,function(pendingIndex,pendingResult)
		{	
		//console.log(pendingResult+1)
		sno=pendingIndex+1;
		//console.log(pendingResult);
		if(pendingResult==1){
           
           
           $.each(unAcknowledgeResult.content.callno,function(pendingHospitalIndex,pendingHospitalCallNo)
           {
           if(pendingIndex == pendingHospitalIndex)
           {
	         pendingdetails+='<div id="'+pendingHospitalCallNo+'contentDiv" class="col-xxl-8	 col-xl-12" ><div class="card info-card sales-card"> <div class="card-body"><h5 class="card-title">'+pendingHospitalCallNo+'</h5><div class="d-flex align-items-center"> <div class="card-icon rounded-circle d-flex align-items-center justify-content-center"><i class="bi bi-clipboard2-pulse-fill"></i>  </div><div class="ps-3"></div>';
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
	         pendingdetails+='<div id="'+pendingrequestCallNo+'ackDiv" class="col-xxl-4 col-xl-12" > <a href="#unacknowledge.html"  value="'+pendingrequestCallNo+'" onclick="acknowledgeHideFunction(this)"><div class="card info-card customers-card " id="endCard"> <div class="card-body" > <h5 class="card-title">'+pendingrequestCallNo+'</h5><div class="d-flex align-items-center"><div class="card-icon rounded-circle d-flex align-items-center justify-content-center"><i class="bi bi-x-circle"></i></div> ';
             pendingdetails+='<div class="ps-3"> <h6 class="endCallText" id="endCard">END Call</h6></div> </div> </div></div> </a>';
           } 
           })
        
               
            pendingdetails+="</div>";
            
            }
      	})
      	$("#nurseack1").html(pendingdetails);
      	
           }
		     });
		       } 
		       
//-----------GET DATA IF DOCTOR CALL ACTIVE ONLY--> END			       
		       



		       
//  acknowledge Div CAll BUTTON  ---> Start
	
var endDateVar="";
var endCallTimeVar= "";
var endDateCallVar= "";
var data="";
	
	
function acknowledgeHideFunction(ackData){

	data = ackData.getAttribute("value");
  //  console.log(data);
    var contentDiv = document.getElementById(data+"contentDiv");
    var ackDiv = document.getElementById(data+"ackDiv");
    contentDiv.style.display = "none";  
    ackDiv.style.display = "none"; 

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
   
 // console.log("EndTime :: "+endDateCallVar+" "+endCallTimeVar);
    
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
			endView:"Nurse"
			 }
	
		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResult)
	    {
		}) 
    
	}	       
		       
//  acknowledge Div CAll BUTTON  ---> END     