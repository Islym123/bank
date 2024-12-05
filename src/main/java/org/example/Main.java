package org.example;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class Account {
    private double balance;
    private final ReentrantLock lock = new ReentrantLock();

    // Конструктор для начального баланса
    public Account(double initialBalance) {
        this.balance = initialBalance;
    }

    // Метод для пополнения счета
    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
            System.out.println("Пополнение: " + amount + ". Текущий баланс: " + balance);
        } finally {
            lock.unlock();
        }
    }

    // Метод для снятия средств
    public boolean withdraw(double amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("Снятие: " + amount + ". Оставшийся баланс: " + balance);
                return true;
            } else {
                System.out.println("Недостаточно средств для снятия " + amount + ". Баланс: " + balance);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    // Метод для проверки текущего баланса
    public double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}

class DepositRunnable implements Runnable {
    private final Account account;

    public DepositRunnable(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            double amount = random.nextInt(1000);
            account.deposit(amount); // Пополняем случайной суммой
            try {
                Thread.sleep(random.nextInt(1500)); // Задержка перед следующим пополнением
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class WithdrawRunnable implements Runnable {
    private final Account account;
    private final double amountToWithdraw;

    public WithdrawRunnable(Account account, double amountToWithdraw) {
        this.account = account;
        this.amountToWithdraw = amountToWithdraw;
    }

    @Override
    public void run() {
        while (!account.withdraw(amountToWithdraw)) {
            try {
                Thread.sleep(1000); // Ожидаем перед повторной попыткой
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


public class Main {
    public static void main(String[] args) {
        Account account = new Account(100000); // Начальный баланс

        // Запуск потока для пополнения
        Thread depositThread = new Thread(new DepositRunnable(account));
        depositThread.start();

        // Запуск потока для снятия
        Thread withdrawThread = new Thread(new WithdrawRunnable(account, 105000));
        withdrawThread.start();
    }
}