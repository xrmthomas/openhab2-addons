# Synop Analyzer Binding

This binding integrates the possibility to download and interpret Synop messages.

## Binding Configuration
 
The binding has no configuration options itself, all configuration is done at 'Things' level.

## Supported Things

There is exactly one supported thing, which represents a Synop message. It has the id `synopanalyzer`.

## Thing Configuration

Besides the Synop Station Number (as ```synopID``` as a [StationID](http://www.ogimet.com/gsynop_nav.phtml.en) string), the second configuration parameter is ```refreshInterval``` which defines the refresh interval in minutes. Synop message are typically updated every hour.


## Channels

The weather information that is retrieved is available as these channels:

| Channel Type ID | Item Type    | Description  |
|-----------------|------------------------|------------- |
| temperature | Number       | The current temperature in degrees Celsius |
| pressure | Number       | The current pressure in millibar (mb) |
| wind-angle | Number       | Wind angle in degrees |
| wind-direction | String       | Wind direction |
| wind-speed-ms | Number       | Wind speed in m/s |
| wind-speed-knots | Number       | Wind speed in knots |
| wind-speed-beaufort | Number       | Wind speed according to Beaufort scale |
| overcast | String       | Appreciation of the cloud cover |
| octa | Number       | Part of the sky covered by clouds (in 8th) |
| time-utc | DateTime       | Observation time of the Synop message |

