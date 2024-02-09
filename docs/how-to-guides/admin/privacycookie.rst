.. _admin_privacycookie:

User interface configuration options
------------------------------------

Privacy page and Terms of Use
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The UI can be configured to show a `Privacy Policy` and `Terms of Use`. These are
displayed under the `Help` section of the UI.

For each of these, the content is read from a ``ConfigMap``. You need to configure
the values in ``ui.client.privacy.page`` to enable the feature and set the reference
ConfigMap name and key. If ``ui.client.privacy.page.enabled`` is ``true``, then the privacy
policy and terms of use will be shown in the UI, with content taken from the ConfigMap
specified by ``ui.client.privacy.page.configMapName`` at the key
``ui.client.privacy.page.configMapPolicyKey`` for the privacy policy and
``ui.client.privacy.page.configMapTermsKey`` for the terms of use.

.. note::

  If you don't set the ConfigMap name and key,
  `a sample <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/templates/ui/ui-client-configmap.yaml>`_
  will be used instead. You can start from it as a template to create your customized ConfigMap.

The `Markdown syntax <https://en.wikipedia.org/wiki/Markdown>`_ is fully supported for the
privacy page content.

Cookie banner
~~~~~~~~~~~~~

Since RenkuLab requires user identification and implements a login system, cookies are
required at all levels to provide basic functionality. Please mind that cookies are also used
for anonymous users (i.e. without an account or not currently logged in). To comply with
international laws, it's strongly advised to explicitly require consent to the user for storing
these data and using cookies.

To activate this feature, please set ``ui.privacy.banner.enabled: true``. We have already configured a
default cookie banner to inform the users about the aforementioned requirements and points to
point them to the privacy page for further details.

It's possible to customize the banner to your own needs, changing both the appearance and the
content. We use `cookie consent <https://github.com/Mastermindzh/react-cookie-consent>`_ to
create the banner and we support customization of all the properties exposed by the library.
All the adjustments should be provided as values in ``ui.privacy.banner`` as follows:

1. ``content``: the HTML content of the cookie banner. Please mind that links should point
   to local content. Use the privacy page to add references to third party sites.
2. ``layout``: the list of properties for the cookies consent plugin. We use
   `Bootstrap <https://getbootstrap.com/docs>`_ for the UI, therefore all the Bootstrap
   classes can be used to customize the appearance.

You can refer to the current
`values.yaml in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/values.yaml>`_
to check the default values and to find an example of how to apply different settings.

Dashboard message
~~~~~~~~~~~~~~~~~

The UI can display a configurable message for logged in users on their dashboard
page, suited for showing general information about Renku.

This feature can be enabled and configured by editing the values found in
``ui.client.dashboardMessage``. Set ``ui.client.dashboardMessage.enabled``
to ``true`` to have the message displayed on the dashboard page.

The content and appearance of the message are configured by editing the other
values in ``ui.client.dashboardMessage``:

1. ``text``: the main message displayed on the dashboard page. It supports
   `Markdown syntax <https://en.wikipedia.org/wiki/Markdown>`_.
2. ``additionalText``: the additional message which users can read by clicking
   "Read more" button right below the main message.
3. ``style``: the appearance of the card used to display the
   dashboard message. Allowed values are: "plain", "success", "info", "warning"
   and "danger".
4. ``dismissible``: if it is set to ``true``, users can dismiss the message and hide it
   until the tab is closed or refreshed.

An example configuration can be found in
`values.yaml in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/values.yaml>`_.
