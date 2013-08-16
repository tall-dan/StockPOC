use Stocks;
insert into Current_Cash values (0.31);
insert into Followed_Stocks(tickerName) values ('amd'), ('aapl'), ('goog'), ('rcl'), ('crdn'), ('nflx');
insert into Owned_Stocks (tickerName, buyPrice, Shares, maxLoss, buyDate) values ('amd', 3.67, 760, 20, NOW());

