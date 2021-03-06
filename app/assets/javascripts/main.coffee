window.onload = ->

  $('.collapsible-accordion').accordion({
    collapsible: true,
    active:      false,
    heightStyle: "content"
  })

  $('.ui-button').each(->
    elem = $(this)
    elem.button()
  )

  $('.ui-tabs').each(->
    elem = $(this)
    elem.tabs()
  )

  $('.checkboxes').each(->
    elem = $(this)
    elem.buttonset()
  )

  $('.check-set').each(->
    elem = $(this)
    elem.buttonset()
  )

  $('.radio-set').each(->
    elem = $(this)
    elem.buttonset()
  )

  $('.check-button').each(->
    elem = $(this)
    elem.button()
  )

  $('.check-label').each(->
    elem = $(this)
    elem.click(->
      btn = $("#" + elem.attr("for"))
      btn[0].checked = !btn[0].checked
      btn.button("refresh")
      btn.change()
      false
    )
  )

  $('.os-button').each(->
    elem = $(this)
    if (!elem[0].checked) then elem.click()
  )

  $('.date-chooser').each(->
    elem = $(this)
    elem.datepicker()
  )

