# Clisk


Clisk is a DSL-based library for procedural image generation that can be used from Clojure and Java.

## Example code and resulting image

    (show (render-lit 
            (seamless vplasma) 
            (v+ (v* 0.2 (seamless 0.2 (rotate 0.1 plasma))) 
                (v* 0.6 vblocks))))

![Voronoi rocks](https://raw.github.com/wiki/mikera/clisk/images/VoronoiRocks.png)

For [more examples see the Wiki](https://github.com/mikera/clisk/wiki)

## Installation

The best way to get started with clisk is to [install it from Clojars](https://clojars.org/net.mikera/clisk) using either leiningen or Maven.

Once you have Clisk specified as a dependency, you should be able to get going with the key functionality as follows:

    (ns my-namespace
      (:use [clisk core functions patterns colours]))
     
    (show (checker red white))

## Features

* A concise DSL for specifying image generators through function composition
* Multi-dimensional texture generation (e.g. 4D textures including time dimension for animations) 
* Fast image synthesis thanks to compiled image generation functions (typically sub-second generation 256*256 4x antialiased textures)
* Anti-aliasing (arbitrary precision)
* A wide variety of patterns and transforms, e.g. Voronoi maps, Perlin Noise
* Easily extensible with your own image generation functions
* Ability to render surfaces with shading based on 3D heightmaps

![Plasma Globe](https://raw.github.com/wiki/mikera/clisk/images/PlasmaGlobe.png)
