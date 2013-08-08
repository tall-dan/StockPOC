use Stocks;
insert into Current_Cash values (0.31);
insert into Followed_Stocks values ('amd'), ('aapl'), ('goog'), ('rcl'), ('crdn'), ('nflx');
insert into Owned_Stocks (Ticker_name, Buy_price, Shares, Max_loss, Buy_date) values ('amd', 3.67, 760, 20, NOW());

