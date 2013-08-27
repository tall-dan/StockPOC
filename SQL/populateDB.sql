use Stocks;
insert into Current_Cash values (0.31);
insert into Followed_Stocks(tickerName) values ('AMD'), ('AAPL'), ('GOOG'), ('RCL'), ('ABT'), ('NFLX');
insert into Owned_Stocks (tickerName, buyPrice, Shares, buyDate) values ('AMD', 3.67, 760, NOW());

