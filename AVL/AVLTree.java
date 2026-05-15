import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AVLTree {

    // ===================== NÓ DA ÁRVORE AVL =====================
    static class Node {
        int value;
        int height;
        Node left, right;

        Node(int value) {
            this.value = value;
            this.height = 1;
        }
    }

    // ===================== ÁRVORE AVL =====================
    static class AVL {
        Node root;

        // Altura do nó
        int height(Node n) {
            return (n == null) ? 0 : n.height;
        }

        // Fator de balanceamento
        int balanceFactor(Node n) {
            return (n == null) ? 0 : height(n.left) - height(n.right);
        }

        // Atualiza altura
        void updateHeight(Node n) {
            if (n != null)
                n.height = 1 + Math.max(height(n.left), height(n.right));
        }

        // ========== ROTAÇÕES ==========

        // Rotação Simples à Direita (LL)
        Node rotateRight(Node y) {
            System.out.println("    [ROTAÇÃO] Rotação Simples à Direita no nó " + y.value);
            Node x = y.left;
            Node T2 = x.right;

            x.right = y;
            y.left = T2;

            updateHeight(y);
            updateHeight(x);

            return x;
        }

        // Rotação Simples à Esquerda (RR)
        Node rotateLeft(Node x) {
            System.out.println("    [ROTAÇÃO] Rotação Simples à Esquerda no nó " + x.value);
            Node y = x.right;
            Node T2 = y.left;

            y.left = x;
            x.right = T2;

            updateHeight(x);
            updateHeight(y);

            return y;
        }

        // Balanceia o nó após inserção/remoção
        Node balance(Node node) {
            updateHeight(node);
            int bf = balanceFactor(node);

            // Caso LL - Rotação Simples à Direita
            if (bf > 1 && balanceFactor(node.left) >= 0) {
                return rotateRight(node);
            }

            // Caso LR - Rotação Dupla Esquerda-Direita
            if (bf > 1 && balanceFactor(node.left) < 0) {
                System.out.println("    [ROTAÇÃO] Rotação Dupla Esquerda-Direita no nó " + node.value);
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }

            // Caso RR - Rotação Simples à Esquerda
            if (bf < -1 && balanceFactor(node.right) <= 0) {
                return rotateLeft(node);
            }

            // Caso RL - Rotação Dupla Direita-Esquerda
            if (bf < -1 && balanceFactor(node.right) > 0) {
                System.out.println("    [ROTAÇÃO] Rotação Dupla Direita-Esquerda no nó " + node.value);
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }

            return node;
        }

        // ========== INSERÇÃO ==========
        Node insert(Node node, int value) {
            if (node == null) return new Node(value);

            if (value < node.value)
                node.left = insert(node.left, value);
            else if (value > node.value)
                node.right = insert(node.right, value);
            else
                return node; // duplicatas ignoradas

            return balance(node);
        }

        void insert(int value) {
            root = insert(root, value);
        }

        // ========== REMOÇÃO ==========
        Node minNode(Node node) {
            while (node.left != null) node = node.left;
            return node;
        }

        Node remove(Node node, int value) {
            if (node == null) return null;

            if (value < node.value)
                node.left = remove(node.left, value);
            else if (value > node.value)
                node.right = remove(node.right, value);
            else {
                // Nó encontrado
                if (node.left == null) return node.right;
                if (node.right == null) return node.left;

                // Dois filhos: substitui pelo menor da subárvore direita
                Node successor = minNode(node.right);
                node.value = successor.value;
                node.right = remove(node.right, successor.value);
            }

            return balance(node);
        }

        void remove(int value) {
            root = remove(root, value);
        }

        // ========== VERIFICAÇÃO AVL ==========
        boolean isAVL(Node node) {
            if (node == null) return true;
            int bf = balanceFactor(node);
            if (bf < -1 || bf > 1) return false;
            return isAVL(node.left) && isAVL(node.right);
        }

        boolean isAVL() {
            return isAVL(root);
        }

        // ========== CONTAGEM DE NÓS ==========
        int countNodes(Node node) {
            if (node == null) return 0;
            return 1 + countNodes(node.left) + countNodes(node.right);
        }

        int size() {
            return countNodes(root);
        }

        // ========== IMPRESSÃO EM ORDEM ==========
        void printInOrder(Node node) {
            if (node == null) return;
            printInOrder(node.left);
            System.out.printf("  Valor: %5d  |  FB: %+2d%n", node.value, balanceFactor(node));
            printInOrder(node.right);
        }

        void printInOrder() {
            System.out.println("  ----------------------------------------");
            System.out.println("  Valor         |  Fator de Balanceamento");
            System.out.println("  ----------------------------------------");
            printInOrder(root);
            System.out.println("  ----------------------------------------");
        }

        // ========== IMPRESSÃO VISUAL DA ÁRVORE ==========
        void printTree(Node node, String prefix, boolean isLeft) {
            if (node == null) return;
            System.out.println(prefix + (isLeft ? "├── " : "└── ") + node.value + " (FB:" + balanceFactor(node) + ")");
            if (node.left != null || node.right != null) {
                printTree(node.left,  prefix + (isLeft ? "│   " : "    "), true);
                printTree(node.right, prefix + (isLeft ? "│   " : "    "), false);
            }
        }

        void printTree() {
            if (root == null) {
                System.out.println("  (árvore vazia)");
                return;
            }
            System.out.println("  " + root.value + " (FB:" + balanceFactor(root) + ")");
            printTree(root.left,  "  ", true);
            printTree(root.right, "  ", false);
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        AVL avl = new AVL();
        Random rand = new Random(42); // seed fixa para reprodutibilidade

        // ===== PASSO 1: Gerar 100 números aleatórios únicos de -500 a 500 =====
        List<Integer> numbers = new ArrayList<>();
        while (numbers.size() < 100) {
            int n = rand.nextInt(1001) - 500; // -500 a 500
            if (!numbers.contains(n)) numbers.add(n);
        }

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           ÁRVORE AVL - Implementação em Java         ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        System.out.println("\n┌─── ETAPA 1: Inserção dos 100 números ───────────────┐");
        System.out.println("  Números gerados aleatoriamente:");
        for (int i = 0; i < numbers.size(); i++) {
            System.out.printf("%5d", numbers.get(i));
            if ((i + 1) % 10 == 0) System.out.println();
        }
        System.out.println();

        // Inserir os 100 números
        System.out.println("\n  Inserindo na árvore AVL (apenas rotações serão exibidas):");
        for (int n : numbers) {
            avl.insert(n);
        }

        // ===== PASSO 2: Verificar se é AVL =====
        System.out.println("\n┌─── ETAPA 2: Verificação da Árvore AVL ─────────────┐");
        System.out.println("  Total de nós inseridos: " + avl.size());
        System.out.println("  A árvore É uma AVL válida? " + (avl.isAVL() ? "✓ SIM" : "✗ NÃO"));

        // ===== PASSO 3: Imprimir todos os nós com valor e FB =====
        System.out.println("\n┌─── ETAPA 3: Nós em Ordem (valor + fator de balance)┐");
        avl.printInOrder();

        // ===== PASSO 4: Estrutura visual da árvore =====
        System.out.println("\n┌─── ETAPA 4: Estrutura Visual da Árvore ────────────┐");
        avl.printTree();

        // ===== PASSO 5: Remover 20 números =====
        System.out.println("\n┌─── ETAPA 5: Remoção de 20 números ─────────────────┐");
        // Embaralha e pega os primeiros 20 para remover
        Collections.shuffle(numbers, rand);
        List<Integer> toRemove = numbers.subList(0, 20);

        System.out.println("  Números a remover:");
        for (int i = 0; i < toRemove.size(); i++) {
            System.out.printf("%5d", toRemove.get(i));
            if ((i + 1) % 10 == 0) System.out.println();
        }
        System.out.println();

        System.out.println("\n  Removendo da árvore AVL (apenas rotações serão exibidas):");
        for (int n : toRemove) {
            avl.remove(n);
        }

        // ===== PASSO 6: Verificar se ainda é AVL após remoções =====
        System.out.println("\n┌─── ETAPA 6: Verificação Após Remoções ─────────────┐");
        System.out.println("  Total de nós restantes: " + avl.size());
        System.out.println("  A árvore AINDA É uma AVL válida? " + (avl.isAVL() ? "✓ SIM" : "✗ NÃO"));

        // ===== PASSO 7: Imprimir nós após remoções =====
        System.out.println("\n┌─── ETAPA 7: Nós em Ordem Após Remoções ────────────┐");
        avl.printInOrder();

        // ===== PASSO 8: Estrutura visual após remoções =====
        System.out.println("\n┌─── ETAPA 8: Estrutura Visual Após Remoções ─────────┐");
        avl.printTree();

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                    FIM DA EXECUÇÃO                  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}