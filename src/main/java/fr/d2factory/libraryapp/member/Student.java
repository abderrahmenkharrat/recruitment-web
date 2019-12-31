package fr.d2factory.libraryapp.member;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.YEARS;

public class Student extends Member {
    @Override
    public void payBook(int numberOfDays) {
        if (YEARS.between(this.getEntryDate(), LocalDate.now()) == 0) {
            this.setWallet((float) (this.getWallet() - (numberOfDays - 15) * 0.1));
        } else {
            this.setWallet((float) (this.getWallet() - (numberOfDays * 0.1)));
        }
    }

}
