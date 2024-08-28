<#macro addBlogTemplate page_id>
<div id="modal-create-blog-page-template" style="display: none; position: absolute; top: 25%; left: 25%; width: 50%; height: 50%; background-color: white; z-index: 1000; border: #2a3746; border-radius: 5px; padding: 20px;">
    <h1>Créer un modèle de page</h1>
        <input type="hidden" name="action" value="addPageTemplate" />
        <input type="hidden" name="page_id" value="${page_id}" />
        <!-- form with input for page template description, html file and a picture -->
        <@formGroup labelFor='page_template_description' labelKey='#i18n{blog.portlet.blogsListBlogsPortlet.labelModelDescription}' rows=2 >
            <@input type='text' name='page_template_description' id='page_template_description' mandatory=true />
        </@formGroup>
        <@formGroup labelFor='page_template_html' labelKey='#i18n{blog.portlet.blogsListBlogsPortlet.labelModelHtml}' rows=2 >
            <@input type='file' name='page_template_file' id='page_template_html' mandatory=true />
        </@formGroup>
        <@formGroup labelFor='page_template_picture' labelKey='#i18n{blog.portlet.blogsListBlogsPortlet.labelModelPicture}' rows=2 >
            <@input type='file' name='page_template_picture_file' id='page_template_picture' mandatory=true />
        </@formGroup>
        <@formGroup labelFor='page_template_portletType' labelKey='#i18n{blog.portlet.blogsListBlogsPortlet.labelModelPortletType}' rows=2 >
            <@select id='page_template_portletType' name='page_template_portlet_type'>
                <@option value='BLOG_PORTLET' label='Blog' />
                <@option value='BLOG_LIST_PORTLET' label='Blog list' />
            </@select>
        </@formGroup>

        <button type="button" class="btn btn-secondary" id="close-modal-create-blog-page-template">Fermer</button>
        <button  class="btn btn-primary" onclick="submitCreateBlogPageTemplateForm()">Créer</button>
</div>
<div  class="btn btn-primary" onclick="toggleModalCreateBlogPageTemplate()">Créer un modèle de page</div>


<script>

    const modalCreateBlogPageTemplate = document.getElementById('modal-create-blog-page-template');


    function toggleModalCreateBlogPageTemplate() {
        modalCreateBlogPageTemplate.style.display = modalCreateBlogPageTemplate.style.display === 'none' ? 'block' : 'none'
        // When the user clicks anywhere outside of the modal, close it
        // stop event propagation
        console.log(this)
        this.addEventListener('click', function(event) {
            event.stopPropagation()
        })
        let clickNumber = 0
        document.addEventListener('click', function(event) {
            if (!modalCreateBlogPageTemplate.contains(event.target) && clickNumber > 0 || event.target.id === 'close-modal-create-blog-page-template') {
                modalCreateBlogPageTemplate.style.display = 'none';
                document.removeEventListener('click', toggleModalCreateBlogPageTemplate)
                clickNumber = 0
            } else {
                clickNumber++
            }
        });
    }
    function submitCreateBlogPageTemplateForm() {
        const form = document.getElementById('createBlogPageTemplateForm')
        const formData = new FormData(form)
        // check if all mandatory fields are filled
        let isFormValid = true
        form.querySelectorAll('input, select').forEach(input => {
            if (input.hasAttribute('mandatory') && !input.value) {
                isFormValid = false
                input.classList.add('is-invalid')
            } else {
                input.classList.remove('is-invalid')
            }
        })
        if (!isFormValid) {
            return
        }
        // submit and get json response
        fetch('/admin/plugins/blog/ManageBlogPageTemplate.jsp&action=addPageTemplate', {
            method: 'POST',
            body: new FormData(form)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log(data)
                  
                }
            })
    }
</script>
</#macro>
