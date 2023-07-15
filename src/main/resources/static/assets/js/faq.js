$.get("innerprocess/etiicos/faq/",function(faqResult){
	
	console.log(faqResult);
	
	var faqTag = "";
	
	$.each(faqResult.content.faqanswer,function(faqAnsIndex,faqAnsResult){
		
		$.each(faqResult.content.faqquestions,function(faqQueIndex,faqQueResult){
		
		if(faqAnsIndex == faqQueIndex)
		{
		
		console.log(faqQueResult+"--"+faqAnsResult)
    
    
		
		faqTag += `<div style="margin-bottom: 2px;" class="card li" >
      <div class="card-header">
        <a style='font-size: 20px;font-weight: 600;font-family: fangsong;' class="btn" data-bs-toggle="collapse" href="#collapse${faqAnsIndex}">
         <p class="demoname">${faqQueResult}</p>
        </a>
      </div>
      <div id="collapse${faqAnsIndex}" class="collapse" data-bs-parent="#accordion">
        <div style='font-size: 20px;font-weight: 200;font-family: fangsong;padding: 20px;' class="card-body">
        ${faqAnsResult}
        </div>
      </div>
    </div>`	
		
		}
	})
	
	})
	$("#faqtag").html(faqTag);	
	})
	
	// Search Function Card Dive -->  Search Start
  // SEARCH FUNCTION
var btsearch = {
	init: function(search_field, searchable_elements, searchable_text_class) {
		$(search_field).keyup(function(e){
			e.preventDefault();
			var query = $(this).val().toLowerCase();
			if(query){
				// loop through all elements to find match
				$.each($(searchable_elements), function(){
					var title = $(this).find(searchable_text_class).text().toLowerCase();
					if(title.indexOf(query) == -1){
						$(this).hide();
					} else {
						$(this).show();
					}
				});
			} else {
				// empty query so show everything
				$(searchable_elements).show();
			}
		});
	}
}

// INIT
$(function(){
  // USAGE: btsearch.init(('search field element', 'searchable children elements', 'searchable text class');
  btsearch.init('#search_field', '.demonames .li', '.demoname');
});


// Search Function Card Dive -->  Search End
	
	