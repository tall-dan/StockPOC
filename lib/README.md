DATE: 05-22-2013

CHANGES:
Accounts SDK:
1. Added TotalCash field in Balance class. This change affects the AccountBalance api response.

Order SDK:
1. Added symbolDescription field in OptionOrderResponse & ChangeOptionOrderResponse classes. This change affects the PreviewOptionOrder, PlaceOptionOrder, PreviewChangeOptionOrder and PlaceChangeOptionOrder api responses.

2. Added support for Advanced orders. New PriceTypes have been added to EquityPriceType(TRAILING_STOP_CNST & OptionPriceType(TRAILING_STOP_CNST) classes. This change will allow users/apps to place advanced orders using java sdks. Affected aPIs are PreviewEquityOrder, PlaceEquityOrder, PreviewChangeEquityOrder, PlaceChangeEquityOrder, PreviewOptionOrder, PlaceOptionOrder, PreviewChangeOptionOrder, PlaceChangeOptionOrder
