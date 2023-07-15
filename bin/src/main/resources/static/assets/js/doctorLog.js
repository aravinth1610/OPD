var databaseID = $("#hospitalid").text();
//console.log($("#hospitalid").text())
 
 
 
 $.post("innerprocess/doctorlog/",{databaseName:databaseID},function(callLogResults){
	 
// console.log(callLogResults)
 var DashboardDetails="";
 let sno=0;
 
	 if (callLogResults.status == 200 && callLogResults.message == "success") {
		
		
		$.each(callLogResults.content.doctorlogcallno,function(logIndex,logResult)
		{
		   sno=sno+1;
		   DashboardDetails+="<tr>";
           DashboardDetails+="<th scope='row'><a href='#'>"+sno+"</a></th>";
           
           
           $.each(callLogResults.content.doctorlogcallno,function(logCallnoIndex,logCallnoResult)
           {
           if(logIndex == logCallnoIndex)
           {
	         
	         DashboardDetails+="<td>"+logCallnoResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.doctorlogdata,function(logContentIndex,logContentResult)
           {
           if(logIndex == logContentIndex)
           {
	         
	         DashboardDetails+="<td>"+logContentResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.doctorlogstarttime,function(startTimeIndex,startTimeResult)
           {
           if(logIndex == startTimeIndex)
           {
	         DashboardDetails+="<td>"+startTimeResult+"</td>";
             } 
              })
              
          $.each(callLogResults.content.doctorlogendtime,function(endTimeIndex,endTimeResult)
           {
           if(logIndex == endTimeIndex)
           {
	         DashboardDetails+="<td>"+endTimeResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.doctorlogduration,function(durationIndex,durationResult)
           {
           if(logIndex == durationIndex)
           {
	         DashboardDetails+="<td>"+durationResult+"</td>"
             } 
              })
         
   
        	$.each(callLogResults.content.doctorlogendview,function(endviewIndex,endviewResult)
           {
           if(logIndex == endviewIndex)
           {
	         DashboardDetails+="<td>"+endviewResult+"</td>"
             } 
              })
            DashboardDetails+="</tr>";
      	
      	})
      	$("#doctorlogdata").html(DashboardDetails);
		
		 $('.mytable').DataTable();
      	};
      	})