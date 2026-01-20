# Website

This website is built using [Docusaurus](https://docusaurus.io/), a modern static website generator.

## Installation

```bash
npm install
```

## Local Development

`cd` into the `docs` folder.

```bash
npm run start
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

Another useful command is `npm run clear` (accompanied by a server restart) which purges the locally
cached and built static files when significant changes are made, but they do not
show up on the development server.

## Updating documentation

All the relevant files are in the `docs/docs` folder. You can add new pages or folders there to modify or add to the documentation.

## Build

```bash
npm run build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service.

## Deployment

The documentation is deployed using [Read The Docs](https://about.readthedocs.com/).
The [renku project](https://app.readthedocs.org/projects/renku/) on Read the Docs
builds and deploys when changes are made in the repo.

## Notes

- When adding an image to a page, put it in the same folder as the page and reference it using a relative path (e.g. `./image.png`). By default, the image will fill the whole page's width. If that's what you want, you're done. Otherwise, you can wrap the image in a container with a custom width based on your desired size, e.g.:
  ```md
  <p class="image-container-s">
  ![image 10](./image-10.png)
  </p>
  ```
  The available container sizes are `image-container-s` (55% width), `image-container-m` (70% width), and `image-container-l` (85% width).
- Image properties that are controllable by CSS can be modified in the documentation CSS file at `docs/src/css/custom.css`. Currently, we use it to add shadows to all images and to define the container sizes for images:

  ```css
  img {
    filter: drop-shadow(0 10px 16px rgba(0, 0, 0, 0.25))
      drop-shadow(0 2px 4px rgba(0, 0, 0, 0.15));
  }

  .image-container-s,
  .image-container-m,
  .image-container-l {
    margin: auto;
    text-align: center; /* Center the image horizontally when its width is narrower than the max-width*/
  }

  .image-container-s {
    max-width: 55%;
  }

  .image-container-m {
    max-width: 70%;
  }

  .image-container-l {
    max-width: 85%;
  }
  ```

- When adding a video to a page, put it in the same folder as the page. You then need to import it in the markdown file using its relative path and put the imported name in the `src` attribute of the `video` tag. For example:

  ```md
  import video10 from './video-10.mp4';

  <video controls width="100%" src={video10} />
  ```

- When linking to other documentation pages, always use relative URL links (e.g. `../sessions/guides/create-environment-with-custom-packages-installed`) instead of absolute links (e.g. `/docs/users/sessions/guides/create-environment-with-custom-packages-installed`) or relative file paths (e.g. `../60-sessions/guides/20-create-environment-with-custom-packages-installed.md`).
  Always use URL links instead of file paths for links. Docusaurus converts file paths into URL links by removing number prefixes and the .md extension. To find the correct URL link, open the corresponding page in your browser, copy the path from the address bar, and adjust it to be relative to the current page.

- If you add an `index.md` file to a directory, never add a prefix to it (e.g. `10-index.md`). Docusaurus treats `index.md` file specially, so it shouldn't have any prefix in its name. Prefixes are only used for other files in the directory to control their order.
