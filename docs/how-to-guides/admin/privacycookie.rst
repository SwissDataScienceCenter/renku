.. _admin_privacycookie:

Configure the user interface
----------------------------

Privacy page
~~~~~~~~~~~~

The UI has a privacy page with a completely configurable content, suited for showing
any policy/terms related information, like the `Privacy Policy Statement` or the
`Terms of Use`.

The content is read from a ``ConfigMap``. You need to configure the values in
``ui.privacy.page`` to enable the feature and set the reference ConfigMap name and key.
Both ``ui.privacy.enabled`` and ``ui.privacy.page.enabled`` need to be ``true`` for
enabling the privacy page.

.. note::

  If you don't set the ConfigMap name and key,
  `a sample <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/templates/configmap.yaml>`_
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

To activate this feature, please set ``ui.privacy.enabled: true``. We have already configured a
default cookie banner to inform the users about the aforementioned requirements and points to
point them to the privacy page for further details.

It's possible to customize the banner to your own needs, changing both the appearance and the
content. We use `cookie consent <https://github.com/Mastermindzh/react-cookie-consent>`_ to
create the banner and we support customization of all the properties exposed by the library.
All the adjustments should be provided as values in ``ui.privacy.banner`` as follows:

1. ``content``: The HTML content of the cookie banner. Please mind that links should point
   to local content. Use the privacy page to add references to third party sites.
2. ``layout``: The list of properties for the cookies consent plugin. We use
   `Bootstrap <https://getbootstrap.com/docs>`_ for the UI, therefore all the Bootstrap
   classes can be used to customize the appearance.

You can refer to the current
`values.yaml in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/values.yaml>`_
to check the default values and to find an example of how to apply different settings.

Dashboard Message
~~~~~~~~~~~~~~~~~

The UI can display a configurable message for logged in users on their dashboard
page, suited for showing general information about Renku.

This feature can be enabled and configured by editing the values found in
``ui.client.dashboardMessage``. Set ``ui.client.dashboardMessage.enabled``
to ``true`` to have the message displayed on the dashboard page.

The content and appearance of the message are configured by editing the other
values in ``ui.client.dashboardMessage``:

1. ``text``: this is the main message displayed on the dashboard page. It supports
   `Markdown syntax <https://en.wikipedia.org/wiki/Markdown>`_.
2. ``additionalText``: this is the additional message which users can read by clicking
   "Read more" just below the main message.
3. ``styles``: this configures the appearance of the card used to display the
   dashboard message. Allowed values are: "plain", "success", "info", "warning"
   and "danger".
4. ``dismissible``: when set to true, users can dismiss the message and hide it
   until the tab is closed or refreshed.

An example configuration can be found in
`values.yaml in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/values.yaml>`_.
