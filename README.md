# Locator Triangulation

<a target="_blank" href="https://github.com/EgorKhabarov/LocatorTriangulation">
    <!-- https://img.shields.io/badge/GitHub-14121c?logo=GitHub&logoColor=white -->
    <svg xmlns="http://www.w3.org/2000/svg" width="65" height="20" role="img" aria-label="GitHub">
        <linearGradient id="s" x2="0" y2="100%"><stop offset="0" stop-color="#bbb" stop-opacity=".1"/><stop offset="1" stop-opacity=".1"/></linearGradient>
        <clipPath id="r"><rect width="65" height="20" rx="3" fill="#fff"/></clipPath>
        <g clip-path="url(#r)">
            <rect width="0" height="20" fill="#555"/>
            <rect x="0" width="65" height="20" fill="#14121c"/>
            <rect width="65" height="20" fill="url(#s)"/>
        </g>
        <g fill="#fff" text-anchor="middle" font-family="Verdana,Geneva,DejaVu Sans,sans-serif" text-rendering="geometricPrecision" font-size="110">
            <image x="5" y="3" width="14" height="14" href="data:image/svg+xml;base64,PHN2ZyBmaWxsPSJ3aGl0ZSIgcm9sZT0iaW1nIiB2aWV3Qm94PSIwIDAgMjQgMjQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHRpdGxlPkdpdEh1YjwvdGl0bGU+PHBhdGggZD0iTTEyIC4yOTdjLTYuNjMgMC0xMiA1LjM3My0xMiAxMiAwIDUuMzAzIDMuNDM4IDkuOCA4LjIwNSAxMS4zODUuNi4xMTMuODItLjI1OC44Mi0uNTc3IDAtLjI4NS0uMDEtMS4wNC0uMDE1LTIuMDQtMy4zMzguNzI0LTQuMDQyLTEuNjEtNC4wNDItMS42MUM0LjQyMiAxOC4wNyAzLjYzMyAxNy43IDMuNjMzIDE3LjdjLTEuMDg3LS43NDQuMDg0LS43MjkuMDg0LS43MjkgMS4yMDUuMDg0IDEuODM4IDEuMjM2IDEuODM4IDEuMjM2IDEuMDcgMS44MzUgMi44MDkgMS4zMDUgMy40OTUuOTk4LjEwOC0uNzc2LjQxNy0xLjMwNS43Ni0xLjYwNS0yLjY2NS0uMy01LjQ2Ni0xLjMzMi01LjQ2Ni01LjkzIDAtMS4zMS40NjUtMi4zOCAxLjIzNS0zLjIyLS4xMzUtLjMwMy0uNTQtMS41MjMuMTA1LTMuMTc2IDAgMCAxLjAwNS0uMzIyIDMuMyAxLjIzLjk2LS4yNjcgMS45OC0uMzk5IDMtLjQwNSAxLjAyLjAwNiAyLjA0LjEzOCAzIC40MDUgMi4yOC0xLjU1MiAzLjI4NS0xLjIzIDMuMjg1LTEuMjMuNjQ1IDEuNjUzLjI0IDIuODczLjEyIDMuMTc2Ljc2NS44NCAxLjIzIDEuOTEgMS4yMyAzLjIyIDAgNC42MS0yLjgwNSA1LjYyNS01LjQ3NSA1LjkyLjQyLjM2LjgxIDEuMDk2LjgxIDIuMjIgMCAxLjYwNi0uMDE1IDIuODk2LS4wMTUgMy4yODYgMCAuMzE1LjIxLjY5LjgyNS41N0MyMC41NjUgMjIuMDkyIDI0IDE3LjU5MiAyNCAxMi4yOTdjMC02LjYyNy01LjM3My0xMi0xMi0xMiIvPjwvc3ZnPg=="/>
            <text aria-hidden="true" x="415" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="370">GitHub</text>
            <text x="415" y="140" transform="scale(.1)" fill="#fff" textLength="370">GitHub</text>
        </g>
    </svg>
</a>
<a target="_blank" href="https://github.com/EgorKhabarov/LocatorTriangulation/actions/workflows/build.yml">
  <img src="https://github.com/EgorKhabarov/LocatorTriangulation/actions/workflows/build.yml/badge.svg" alt="build">
</a>

![icon.png](src/main/resources/assets/locator_triangulation/icon.png)

A client-side Fabric mod that allows determining players' coordinates using triangulation.

### Quick start

* Run `/locator_pos1` — the mod will remember the locator's state, your current position and your view direction (yaw).
* Move away as far as possible so the angle to the target becomes as large as possible, then run `/locator_pos2`.
* Run:

  * `/locator_locate <name>` — attempt to compute the coordinates of player `<name>`, or
  * `/locator_locate_all` — attempt to compute coordinates of all known targets.

The larger the angle between directions from the two points, the more accurate the coordinates.
For regular triangulation using your coordinates and your view direction, use the commands with the `/triangulation` prefix.

### Command description

#### Locator commands

For regular triangulation use the same commands but with the `triangulation` prefix.

The `/locator_data` command will print the locator's contents.

* `/locator_data` — print current locator data
* `/locator_pos1` — save the locator's first position, and your coordinates + view yaw
* `/locator_pos2` — save the locator's second position, and your coordinates + view yaw
* `/locator_get_poses` — print the saved positions
* `/locator_clear_pos1` — clear the first position
* `/locator_clear_pos2` — clear the second position
* `/locator_clear_poses` — clear both positions
* `/locator_locate <name>` — compute coordinates of player `<name>` (if both positions are saved)
* `/locator_locate_all` — compute coordinates for all known targets (if both positions are saved)

#### Regular triangulation commands

* `/triangulation_pos1` — save the first position (coordinates + yaw)
* `/triangulation_pos2` — save the second position (coordinates + yaw)
* `/triangulation_get_poses` — print the saved positions
* `/triangulation_clear_pos1` — clear the first position
* `/triangulation_clear_pos2` — clear the second position
* `/triangulation_clear_poses` — clear both positions
* `/triangulation_locate` — compute the intersection coordinates of the view directions (if both positions are saved)

### Accuracy notes

Triangulation accuracy strongly depends on the angle between view directions from the two positions.
Good results are achieved with an angle between directions from 20° to 150°.
Near-perfect accuracy occurs at about 70–100°.
If the angle is too small (less than 20°) or too large (150° and above), accuracy drops significantly.

For maximum accuracy, take positions so that the target is roughly at a right angle relative to you.

### How to hide from the locator

There are several ways to hide from the radar: sneaking (Shift), a mob's or another player's head, a carved pumpkin, or an invisibility potion.
