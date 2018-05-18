define([
  "base/js/namespace",
  "base/js/dialog",
  "base/js/utils",
  "jquery"
], function(IPython, dialog, utils, $, mc) {
  var gitCommitPush = {
    help: 'Commit current notebook and share it.',
    icon: 'fa-gitlab',
    help_index: '',
    handler: function (env) {
      var p = $('<p/>').text('Please enter your commit message. Only this notebook will be committed.')
      var input = $('<textarea rows="4" cols="72"></textarea>')
      var div = $('<div/>')
      var commitUrl = env.notebook.base_url + '/git/commit'

      div.append(p).append(input)

      // get the canvas for user feedback
      var container = $('#notebook-container')

      function onOk () {
        var re = /\/notebooks(.*?)$/
        var filepath = window.location.pathname.match(re)[1]
        var payload = {'filename': filepath, 'msg': input.val()}
        var settings = {
          url: commitUrl,
          processData: false,
          type: 'PUT',
          dataType: 'json',
          data: JSON.stringify(payload),
          contentType: 'application/json',
          success: function (data) {
            // display feedback to user
            var container = $('#notebook-container')
            var feedback = '<div class="commit-feedback alert alert-success alert-dismissible" role="alert">' +
              '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
              '<span aria-hidden="true">&times;</span></button>' +
              data.statusText +
              '</div>'

            // display feedback
            $('.commit-feedback').remove()
            container.prepend(feedback)
          },
          error: function (data) {
            // display feedback to user
            var feedback =
              '<div class="commit-feedback alert alert-danger alert-dismissible" role="alert">' +
              '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
              '<strong>Warning!</strong> Something went wrong.<div>' + data.statusText + '</div></div>'

            // display feedback
            $('.commit-feedback').remove()
            container.prepend(feedback)
          }
        }

        // display preloader during commit and push
        var preloader = '<img class="commit-feedback" src="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.5.8/ajax-loader.gif">'
        container.prepend(preloader)

        // commit and push
        utils.ajax(settings)
      }

      if (IPython.notebook.dirty === true) {
        dialog.modal({
          body: 'Please, save the notebook before sharing.',
          title: 'Action required',
          buttons: {
            'Back': {}
          },
          notebook: env.notebook,
          keyboard_manager: env.notebook.keyboard_manager
        })
      } else {
        dialog.modal({
          body: div,
          title: 'Commit and Push Notebook',
          buttons: {
            'Commit and Push': {
              class: 'btn-primary btn-large',
              click: onOk
            },
            'Cancel': {}
          },
          notebook: env.notebook,
          keyboard_manager: env.notebook.keyboard_manager
        })
      }
    }
  }

  function _onLoad () {
    // log to console
    console.info('Loaded Jupyter extension: Git Commit and Push')
    // register new action
    var actionName = IPython.keyboard_manager.actions.register(
      gitCommitPush, 'commit-push', 'jupyter-gitlab')

    console.info(actionName)
    // add button for new action
    IPython.toolbar.add_buttons_group([actionName])
  }

  return {load_ipython_extension: _onLoad}
})
