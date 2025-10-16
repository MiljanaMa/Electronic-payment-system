export interface Transaction{
    id: number;
    senderWalletId: string;
    receiverWalletId: string;
    amount: number;
    transactionHash: string;
}