package fr.d2factory.libraryapp.member;

public class Resident extends Member {

    @Override
    public void payBook(int numberOfDays) {
        if (numberOfDays > 60)
            this.setWallet((float) (this.getWallet() - (numberOfDays - 60) * 0.2 - (60 * 0.1)));
        else
            this.setWallet((float) (this.getWallet() - (numberOfDays * 0.1)));
    }

}
