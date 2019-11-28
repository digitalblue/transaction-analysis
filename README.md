## Coding Challenge
#### Ed Ras

#### How to build and run
Clone this repository and `cd` into the folder. To run:
* `mvn compile`
* `mvn clean package`
* `java -jar target/transaction-analysis-1.0-SNAPSHOT.jar`

Please note, if above fails please try to build and run in an IDE like Intellij.

You will be prompted to enter account number, dates and times. Example output result as per below.
```
Enter account number:ACC334455
Enter start date:20/10/2018
Enter start time:12:00:00
Enter end date:21/10/2018
Enter end time:12:00:00
accountId: ACC334455
from: 20/10/2018 12:00:00
to: 21/10/2018 12:00:00
Relative balance for the period is: -$32.25
Number of transactions included is: 2
```
Please note that validation on the user input has not been implemented.
* Enter dates in DD/MM/YYYY format, eg: 30/12/2019
* Enter times in HH:MM:SS format, eg: 16:30:59

To change the csv input file please update the following in App.java

```private static final String TRANSACTION_FILE = "./transactions.csv";```

#### Design approach
The design approach was to break the analysis of the transactions into smaller reusable methods that perform a single task.
For example:
* getRelativeBalance only returns the balance for a specified account id in a transaction list. It is not concerned if the transactions fall within a specific date range. Another method handles that.

