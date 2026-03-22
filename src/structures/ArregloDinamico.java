package structures;

public class ArregloDinamico<T> {
    private T[] elements;
    private int size;
    private int length;

    public ArregloDinamico(int m){
        this.length = 0;
        this.size = m;
        this.elements = (T[]) new Object[size];
    }

    private void grow(){
        if (length == size){
            int m = (int)(size*1.618);
            T[] newArray = (T[]) new Object[m];
            for (int i = 0; i < size; i++) {
                newArray[i] = elements[i];
            }
            elements = newArray;
            size = m;
        }
    }

    private boolean insert(int index, T data){
        if (0 <= index && index <= length){
            grow();
            for(int i = length; i > index; i--){
                elements[i] = elements[i - 1];
            }
            elements[index] = data;
            length++;
            return true;
        }
        return false;
    }

    private void append(T data){
        insert(length, data);
    }

    private void shrink(){
        if(length <= (int)(size/(1.618*1.618)) && size > 20){
            int m = (int) (size/1.618);
            T[] newArray = (T[]) new Object[m];
            for(int i = 0; i < m; i++){
                newArray[i] = elements[i];
            }
            elements = newArray;
            size = m;
        }
    }

    private boolean remove(int index){
        if (0 <= index && index < length){
            length--;
            for(int i = index; i < length; i++){
                elements[i] = elements[i + 1];
            }
            shrink();
            return true;
        }
        return false;
    }

    private boolean del(){
        return remove(length-1);
    }

    private int len(){
        return length;
    }

    private void mostrarElemento(int index){
        System.out.print(elements[index]);
    }

    private void mostrarArreglo(){
        if (length == 0) {
            System.out.println("[]");
            return;
        }
        System.out.print("[");
        for(int i = 0; i < (length - 1); i++){
            System.out.print(elements[i] + ", ");
        }
        System.out.print(elements[length - 1] + "]");
    }
}
