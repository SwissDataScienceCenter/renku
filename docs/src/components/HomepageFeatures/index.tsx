import type { ReactNode } from "react";
import clsx from "clsx";
import Heading from "@theme/Heading";
import styles from "./styles.module.css";

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<"svg">>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: "Where research comes together",
    Svg: require("@site/static/img/search.svg").default,
    description: (
      <>
        Integrations galore! We support integrations for your favorite code and
        data platforms, so you can connect your whole project in one place.
      </>
    ),
  },
  {
    title: "Built for every researcher",
    Svg: require("@site/static/img/heart.svg").default,
    description: (
      <>
        Whether you're comfortable with command lines or prefer graphical
        interfaces, Renku adapts to your working style.
      </>
    ),
  },
  {
    title: "Effortless collaboration",
    Svg: require("@site/static/img/people.svg").default,
    description: (
      <>
        Share your Renku project with anyone, and never worry about “it doesn’t
        work on my machine” again.
      </>
    ),
  },
];

function Feature({ title, Svg, description }: FeatureItem) {
  return (
    <div className={clsx("col col--4")}>
      <div className="text--center">
        <Svg className={styles.featureSvgSmall} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
