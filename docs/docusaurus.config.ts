import type * as Preset from "@docusaurus/preset-classic";
import type { Config } from "@docusaurus/types";
import { themes as prismThemes } from "prism-react-renderer";

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const url = new URL(
  process.env.READTHEDOCS_CANONICAL_URL || "https://renku.readthedocs.io",
);

const rtdVersion = process.env.READTHEDOCS_VERSION || 'local';

const algoliaConfig = process.env.ALGOLIA_APP_ID
  ? ({
      // The application ID provided by Algolia
      appId: process.env.ALGOLIA_APP_ID,

      // Public API key: it is safe to commit it
      apiKey: process.env.ALGOLIA_API_KEY,

      indexName: "renkulab docs",

      // Optional: see doc section below
      contextualSearch: true,

      // Optional: Specify domains where the navigation should occur through window.location instead on history.push. Useful when our Algolia config crawls multiple documentation sites and we want to navigate with window.location.href to them.
      // externalUrlRegex: 'external\\.com|domain\\.com',

      // Optional: Replace parts of the item URLs from Algolia. Useful when using the same search index for multiple deployments using a different baseUrl. You can use regexp or string in the `from` param. For example: localhost:3000 vs myCompany.com/docs
      replaceSearchResultPathname: {
        from: "/en/[a-zA-Z0-9_-]+/",
        to: "/",
      },

      // Optional: Algolia search parameters
      // searchParameters: {},

      // Optional: path for search page that enabled by default (`false` to disable it)
      // searchPagePath: 'search',

      // Optional: whether the insights feature is enabled or not on Docsearch (`false` by default)
      insights: false,

      // Optional: whether you want to use the new Ask AI feature (undefined by default)
      // askAi: 'YOUR_ALGOLIA_ASK_AI_ASSISTANT_ID',

      //... other Algolia params
    } satisfies Preset.ThemeConfig["algolia"])
  : undefined;

const config: Config = {
  title: "Renku",
  tagline: "Connecting data, code, compute, and people.",
  favicon: "img/favicon.ico",

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
  organizationName: "SwissDataScienceCenter", // Usually your GitHub org/user name.
  projectName: "renku", // Usually your repo name.

  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",

  markdown: { mermaid: true },

  themes: ["@docusaurus/theme-mermaid"],

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  presets: [
    [
      "classic",
      {
        docs: {
          sidebarPath: "./sidebars.ts",
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl: "https://github.com/SwissDataScienceCenter/renku/docs",
        },
        theme: {
          customCss: "./src/css/custom.css",
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: "img/docusaurus-social-card.jpg",
    navbar: {
      title: "Docs",
      logo: {
        alt: "My Site Logo",
        src: "img/logo.svg",
      },
      items: [
        {
          type: "docSidebar",
          sidebarId: "users",
          position: "left",
          label: "For users",
        },
        {
          type: "docSidebar",
          sidebarId: "admins",
          position: "left",
          label: "For admins",
        },
        {
          href: "https://renkulab.io",
          label: "Renkulab",
          position: "right",
        },
        {
          href: "https://github.com/SwissDataScienceCenter/renku",
          label: "GitHub",
          position: "right",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Docs",
          items: [
            {
              label: "For users",
              to: "/docs/users",
            },
            {
              label: "For admins",
              to: "/docs/admins/architecture/services",
            },
          ],
        },
        {
          title: "Community",
          items: [
            {
              label: "Forum",
              href: "https://renku.discourse.group",
            },
            {
              label: "Chat (Gitter)",
              href: "https://gitter.im/SwissDataScienceCenter/renku",
            },
            {
              label: "Blog",
              href: "https://blog.renkulab.io",
            },
            {
              label: "Email",
              href: "mailto:hello@renku.io",
            },
          ],
        },
        {
          title: "Follow us",
          items: [
            {
              label: "Youtube",
              href: "https://www.youtube.com/@renkuio1503",
            },
            {
              label: "Mastodon",
              href: "https://fosstodon.org/@renku",
            },
          ],
        },
        {
          title: "About",
          items: [
            {
              label: "Renkulab.io",
              href: "https://renkulab.io",
            },
            {
              label: "Roadmap",
              href: "https://renku.notion.site/Roadmap-b1342b798b0141399dc39cb12afc60c9",
            },
            {
              label: "GitHub",
              href: "https://github.com/SwissDataScienceCenter/renku",
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
    ...(algoliaConfig ? { algolia: algoliaConfig } : {}),
    metadata: [
      { name: 'docsearch:version', content: rtdVersion },
      { name: 'docsearch:docusaurus_tag', content: `docs-default-${rtdVersion}` },
    ],
  } satisfies Preset.ThemeConfig,
};

export default config;
