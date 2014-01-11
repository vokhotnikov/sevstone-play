$(".divlink").click(() -> 
  window.location=$(this).attr("data-href")
  return false
)