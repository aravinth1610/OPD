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


var databaseID = $("#hospitalid").text();
 
 $.post("innerprocess/recipientlog/",{databaseName:databaseID},function(callLogResults){
	 
 var DashboardDetails="";
 let sno=0;
 
	 if (callLogResults.status == 200 && callLogResults.message == "success") {
		
		
		$.each(callLogResults.content.recipientlogcallno,function(logIndex,logResult)
		{
		   sno=sno+1;
		   DashboardDetails+="<tr>";
           DashboardDetails+="<th scope='row'><a href='#'>"+sno+"</a></th>";
              
           $.each(callLogResults.content.recipientdoctorname,function(doctornameIndex,doctornameResult)
           {
           if(logIndex == doctornameIndex)
           {
	         
	         DashboardDetails+="<td>"+doctornameResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogdata,function(logContentIndex,logContentResult)
           {
           if(logIndex == logContentIndex)
           {
	         
	         DashboardDetails+="<td>"+logContentResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientdepartment,function(recipientDepartmentIndex,recipientDepartmentResult)
           {
           if(logIndex == recipientDepartmentIndex)
           {
	         
	         DashboardDetails+="<td>"+recipientDepartmentResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientroomno,function(recipientRoomnoIndex,recipientRoomnoResult)
           {
           if(logIndex == recipientRoomnoIndex)
           {
	         
	         DashboardDetails+="<td>"+recipientRoomnoResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogstarttime,function(startTimeIndex,startTimeResult)
           {
           if(logIndex == startTimeIndex)
           {
	         DashboardDetails+="<td>"+startTimeResult+"</td>";
             } 
              })
              
          $.each(callLogResults.content.recipientlogendtime,function(endTimeIndex,endTimeResult)
           {
           if(logIndex == endTimeIndex)
           {
	         DashboardDetails+="<td>"+endTimeResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogduration,function(durationIndex,durationResult)
           {
           if(logIndex == durationIndex)
           {
	         DashboardDetails+="<td>"+durationResult+"</td>"
             } 
              })
         
   
        	$.each(callLogResults.content.recipientlogendview,function(endviewIndex,endviewResult)
           {
           if(logIndex == endviewIndex)
           {
	         DashboardDetails+="<td>"+endviewResult+"</td>"
             } 
              })
            DashboardDetails+="</tr>";
      	
      	})
      	$("#recipientlogdata").html(DashboardDetails);
		
		 $('.mytable').DataTable();
      	};
      	})
      	
      	$("#nurseCallLogDwnBtn").on('click',function(){

var hospitalID = $("#hospitalid").text();

$.post("innerprocess/exceldwnrecipientlog/",{hospitalName:hospitalID},function(nurseCallLogExcelDownloadResult){
	
	if(nurseCallLogExcelDownloadResult.status == 200 && nurseCallLogExcelDownloadResult.message == 'success')
	{

//ReFormate Database Date --> Start 
function getDate(formateDate) {
    var d = (new Date(formateDate) + '').split(' ');
    d[2] = d[2] + ',';
    return [d[0], d[1], d[2], d[3]].join(' ');
}

var formateDate = Date.parse(new Date());
var today = getDate(formateDate);

var date=today.slice(8,10);
var month=today.slice(4,7);
var year=today.slice(12,16);
var reFormatedDate = date+" "+month+" "+year;
		
	var callLogDetails = nurseCallLogExcelDownloadResult.content;
		
	// Create a new workbook
const workbook = XLSX.utils.book_new();

// Convert the array to a worksheet
const worksheet = XLSX.utils.aoa_to_sheet(callLogDetails);

// Add the worksheet to the workbook
XLSX.utils.book_append_sheet(workbook, worksheet, 'My Sheet');

// Write the workbook to a file
XLSX.writeFile(workbook, 'OPDRecipient Call Log '+reFormatedDate+'.xlsx');
	
	}else{}
	
})
})
      	
      	