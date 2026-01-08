import { themes as prismThemes } from 'prism-react-renderer';
import type { Config } from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const url = new URL(process.env.READTHEDOCS_CANONICAL_URL || "https://renku.readthedocs.io");

const config: Config = {
  title: 'Renku',
  tagline: 'Connecting data, code, compute, and people.',
  favicon: 'img/favicon.ico',

  // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
  future: {
    v4: true, // Improve compatibility with the upcoming Docusaurus v4
  },

  // Set the production url of your site here
  url: `http://${url.host}`,
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: url.pathname,

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'SwissDataScienceCenter', // Usually your GitHub org/user name.
  projectName: 'renku', // Usually your repo name.

  onBrokenLinks: 'throw',
  
  markdown: { mermaid: true,
    hooks: {
      onBrokenMarkdownLinks: 'warn',
    }
  },

  themes: [
    '@docusaurus/theme-mermaid',
  ],

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/SwissDataScienceCenter/renku/docs',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: 'img/docusaurus-social-card.jpg',
    navbar: {
      title: 'Docs',
      logo: {
        alt: 'My Site Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'users',
          position: 'left',
          label: 'For users',
        },
        {
          type: 'docSidebar',
          sidebarId: 'admins',
          position: 'left',
          label: 'For admins',
        },
        {
          href: 'https://renkulab.io',
          label: 'Renkulab',
          position: 'right',
        },
        {
          href: 'https://github.com/SwissDataScienceCenter/renku',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'For users',
              to: '/docs/users',
            },
            {
              label: 'For admins',
              to: '/docs/admins/architecture/services',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Forum',
              href: 'https://renku.discourse.group',
            },
            {
              label: 'Chat (Gitter)',
              href: 'https://gitter.im/SwissDataScienceCenter/renku',
            },
            {
              label: 'Blog',
              href: 'https://blog.renkulab.io',
            },
            {
              label: 'Email',
              href: 'mailto:hello@renku.io',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Renkulab',
              href: 'https://renkulab.io',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/SwissDataScienceCenter/renku',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Swiss Data Science Center`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
