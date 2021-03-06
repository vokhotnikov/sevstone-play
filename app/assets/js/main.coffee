$(".divlink").click(() -> 
  window.location=$(this).attr("data-href")
  return false
)

$(".submit-on-change").change(() -> this.form.submit())

$(".submit-on-enter").keypress((event) ->
  if (event.which == 13)
    event.preventDefault()
    this.form.submit())
    
$(".chosen-select").chosen({
  inherit_select_classes: true,
  disable_search_threshold: 5,
  search_contains: true,
  no_results_text: "Нет результатов:" 
})