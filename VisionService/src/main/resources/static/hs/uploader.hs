behavior Uploader(url, accept, imgId)

init
	add .uploader to me

	set element form to the first <form/> in me
	set element input to the first <input[type="file"]/> in me
    
	call htmx.process(element form)
	
on dragenter
	halt the event
	add .highlight

on dragover
	halt the event
	add .highlight

on dragleave
	halt the event
	remove .highlight

on drop(dataTransfer)
    log "dropped"
	halt the event
	remove .highlight
	set element input.files to dataTransfer.files
    trigger change on me
    trigger submit on element form 

on change
    set input to the element input
    set outImage to the element outImage
    js(input, imgId) 
        const files = input.files;
         if (FileReader && files && files.length) {
            var fr = new FileReader();
            fr.onload = function () {
                document.getElementById(imgId).src = fr.result;
            }
            fr.readAsDataURL(files[0]);
        }
    end
 

on htmx:xhr:progress
	-- log event
end