

//-----------------------------GET Data Settings-------------------Start
var dbsID = $("#hospitalid").text();
//console.log($("#hospitalid").text())


$.post("innerprocess/recipientdatas/",{databaseName : dbsID},function(deleteDataResult)
	    {
			   // console.log("call Card No : "+deleteDataResult.content.callno);	
				//console.log("Doctor Data  : "+deleteDataResult.content.doctordata);
				getDataSettings();
		});	
	
	
		
function getDataSettings()
	{
	
    $.post("innerprocess/recipientdatas/",{databaseName : dbsID},function(getDataSettingsResult){

    var dataSettings="";
    var sno="";
	 if (getDataSettingsResult.status == 200 && getDataSettingsResult.message == "success") {
	
		$.each(getDataSettingsResult.content.callno,function(cardIDIndex,pendingResult)
		{	
			sno=cardIDIndex+1;
		
           $.each(getDataSettingsResult.content.callno,function(cardIdHospitalIndex,cardIdHospitalResult)
           {
           if(cardIDIndex == cardIdHospitalIndex)
           {
	         dataSettings+='<div id="'+cardIdHospitalResult+'" value="'+cardIdHospitalResult+'" class="row" ><div class="col-lg-12"><div class="card"><div class="card-body"><h5 class="card-title" >'+cardIdHospitalResult+'';
	         dataSettings+='</h5> <textarea id="textArea'+cardIdHospitalResult+'" ';
            } 
            })
                
           $.each(getDataSettingsResult.content.doctordata,function(contenrequestIndex,contentrequestResult)
           {
			   
           if(cardIDIndex == contenrequestIndex)
           {
			  dataSettings+=' placeholder="Edit & Enter You Want..." id="'+contentrequestResult+'"  spellcheck="true" readonly="readonly" rows="4" cols="32">'+contentrequestResult+'</textarea>'; 
           } 
           })
            
            $.each(getDataSettingsResult.content.callno,function(cardIdHospitalIndex,cardIdHospitalResult)
           {
           if(cardIDIndex == cardIdHospitalIndex)
           {
	         dataSettings+='<br><br><button onclick="editCard(this)" value="'+cardIdHospitalResult+'" id="textArea'+cardIdHospitalResult+'" '+'   type="button" class="btn btn-light" style="margin-right:30px" ><i class="bi bi-pencil-square"></i></button>';
             dataSettings+='<button onclick="saveCard(this)" value="'+cardIdHospitalResult+'"  type="button" class="btn btn-success" style="margin-right:30px" ><i class="bi bi-check-circle"></i></button>';
             dataSettings+='<button onclick="deleteCard(this)" id="'+cardIdHospitalResult+'" value="'+cardIdHospitalResult+'" type="button" class="btn btn-danger" style="margin-right:0px"><i class="bi bi-trash"></i></button>';
            //  pendingdetails+='<button onclick="nurseCallCard(this)" value="'+pendingrequestResult+'"  type="button" class="btn btn-primary" style="margin-right:16px"><i class="fa-solid fa-user-nurse"></i> Call</button>';
           
           } 
           })        
            dataSettings+="</div></div></div></div>";
      	})
      	$("#addPanel").html(dataSettings);
      	
           }
		});
    } 
//-----------------------------GET Data Settings--------------------Stop		       












//count --> card id data
var count =""; 
 
// PlusButton Data Value ofCard  --> Start
$("#addButton").click(function(){
		
	$.get("innerprocess/doctor/newcardrequest/",function(doctorDashboard){
		if (doctorDashboard.status == 200) {
	// console.log("createData ID :: "+doctorDashboard.message);
	 count =doctorDashboard.message; 
	
	 
	 
var divisionStart = '<div id="'+count+'" value="'+count+'"';
var divisionStart2='" class="row" ><div class="col-lg-12"><div class="card"><div class="card-body">';
var cardTitle = '<h5 class="card-title" >';
var cardNo = ''+count+' <a id="infoCardGreen'+count+'"></a>';
var cardNo2 = ' <a id="infoCardRed'+count+'"></a>';
var textArea = ' </h5> <textarea placeholder="Edit & Enter You Want..." id="textArea'+count+'" '+' spellcheck="true" readonly="readonly" rows="4" cols="32"></textarea>'
var editBtn = '<br><br><button onclick="editCard(this)" value="'+count+'" type="button" class="btn btn-light" style="margin-right:30px" ><i class="bi bi-pencil-square"> </i></button>';
var saveBtn = '<button onclick="saveCard(this)" value="'+count+'"  type="button" class="btn btn-success" style="margin-right:30px" ><i class="bi bi-check-circle"> </i></button>';
var deleteBtn = '<button'; 
var deleteBtnID = ' onclick="deleteCard(this)" id="'+count+'"';
var deletebtn2 = ' value="'+count+'" type="button" class="btn btn-danger" style="margin-right:16px"><i class="bi bi-trash"> </i></button>';
var callBtn = '<button onclick="nurseCallCard(this)" value="'+count+'"  type="button" class="btn btn-primary" style="margin-right:30px"><i class="fa-solid fa-user-nurse"></i> Call</button>';
var divisionEnd='</div></div></div></div>';

$("#addPanel").append(divisionStart+divisionStart2+cardTitle+cardNo+cardNo2+textArea+editBtn+saveBtn+deleteBtn+deleteBtnID+deletebtn2+divisionEnd);
 }else{
	 console.log("error");
 }
    });
    
});
// PlusButton Data Value ofCard  --> End





// Delete Data Value ofCard  --> Start

var data= "";
function deleteCard(deleteCard){
	         data = deleteCard.getAttribute("value");
	         var database = document.getElementById('hospitalid').innerHTML;
             document.getElementById(data).style.display = "none";
    
    	var deleteData={
	     	dataBaseName: database,
			tableName:data
			 }

//console.log("Delete ID   :"+deleteData.tableName);	
	$.post("innerprocess/doctor/deletecardRequest/",deleteData,function(deleteDataResult)
	    {
		});
}
// Delete Data Value ofCard  --> End




// Save Data Value ofCard  --> Start

function saveCard(saveCard){
	     data = saveCard.getAttribute("value");
	     var database = document.getElementById('hospitalid').innerHTML;
         var textAreaDataID ="#textArea"+data;		
	     var typedata = $(textAreaDataID).val();
    
     //console.log(typedata);
    	var saveData={
	     	dataBaseName: database,
			callNoValue:data,
	        typedData:typedata
	             }
//console.log("Save ID :"+saveData.callNoValue);	

    $.post("innerprocess/doctor/typedRequest/",saveData,function(saveDataResult)
	    {
		});
}
// Save Data Value ofCard  --> End





// Edit Data Value ofCard  --> Start
function editCard(editCard){
	       data = editCard.getAttribute("value");
	       var editDataID = 'textArea'+data;
	     //  console.log("Edit Data :: "+data)
		   document.getElementById(editDataID).readOnly=false;
		   document.getElementById(editDataID).disabled = false;
}
// Edit Data Value ofCard  --> End







// nurse Call Data Value ofCard  --> Start

function nurseCallCard(NurseCallCard){
	
	
	
    var infoButtongreen = '<button id="'+data+'infoAckRed" type="button" class="btn btn-success" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';
	var infoButtonred = '<button id="'+data+'infoAckRed" type="button" class="btn btn-danger" style="margin-right:16px"><i class="bi bi-info-circle"></i></button>';
	data = NurseCallCard.getAttribute("value");	
	var database = document.getElementById('hospitalid').innerHTML;

	
	var infoCardGreen = '#infoCardGreen'+data; 
	var infoCardRed = '#infoCardRed'+data; 

	
			var ActiveData={
	     	dataBaseName: database,
			tableName:data
			 }
console.log("nurseCall ActiveData Data : "+ActiveData);	
		var callData={
	     	dataBaseName: database,
			status:true,
			callno:data
			 }
console.log("nurseCall Data : "+ActiveData);	
	
		$.post("innerprocess/doctor/nursecall/",callData,function(callDataResult)
	    {
		})
	

	$.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	    {
    console.log("TRUE :: "+activeDataResult.content);
	if(activeDataResult.content==true){
		
		if(infoButtongreen==$(infoCardGreen).html()){
		         $(infoCardGreen).remove();
			     $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtonred==$(infoCardGreen).html()){
		         $(infoCardGreen).remove();
			     $(infoCardRed).append(infoButtonred);
			}	
			
		if(infoButtongreen!=$(infoCardGreen).html()){
			      $(infoCardGreen).append(infoButtongreen);
		}
		
		
			
var cardIDRefresh ="#"+data;	
             
			if(activeDataResult.content==true)
               {
                 $(cardIDRefresh).ready(function()
                      {
	                  setInterval(function(){
		                                  postDataCard(cardIDRefresh);
		                                  },5000);
	                  });
                }



function postDataCard(dataID)
{
	        $.post("innerprocess/doctor/nurseactivatestatus/",ActiveData,function(activeDataResult)
	                 {	
						console.log("FALSE :: "+activeDataResult.content);	
	                    if(activeDataResult.content==false){ 		
		                var infoButtonRed =dataID+"infoAckRed";
		                $(infoButtonRed).css('background','#DC3545');
	                                                        }
	                 })
};
  		
}else{		
		 console.log($(infoCardGreen).html());
	}
		
  })
}

// nurse Call Data Value ofCard  --> End



















