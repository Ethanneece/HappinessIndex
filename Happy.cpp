#include "Happy.h"
#include "SWI-cpp.h"

bool initialized = false;

void init() {
    const char* av[2];
    av[0] = "libswipl.so";
    av[1] = "-q";
    //the following two lines are a workaround 
    const char* dir = "SWI_HOME_DIR=/usr/bin/swipl";
    putenv((char*)dir); 
    PL_initialise(2, (char**)av);
    

	// Consult prolog knowledge bases
    PlCall("consult('happy.pl')");
	PlCall("consult('data.pl')");
}


/*
 * Class:     Happy
 * Method:    callPrologHappyIndex
 * Signature: (Ljava/lang/String;I)D
 */
JNIEXPORT jdouble JNICALL Java_Happy_callPrologHappyIndex
  (JNIEnv * env, jobject, jstring person, jint priceIndex) {
    
    if (!initialized) {
		init();
		initialized = true;
	}

	term_t p, n, y, ans; 
	functor_t funct; 

	// Convert java string to char array.
	jboolean iscopy;
	const char* strPerson = env->GetStringUTFChars(person, &iscopy);
	const int price = (int) priceIndex;

	// Set up prolog variables
	p = PL_new_term_ref();
	PL_put_atom_chars(p, strPerson);
	n = PL_new_term_ref();
    PL_put_integer(n, price);
    y = PL_new_term_ref(); 
	ans = PL_new_term_ref();

	funct = PL_new_functor(PL_new_atom("happy_index"), 3);
	PL_cons_functor(ans, funct, p, n, y);
	module_t happy_pl = PL_new_module(PL_new_atom("happy.pl"));

	// Call function and return result
	double num = 0; 
	if(PL_call(ans, happy_pl))
		PL_get_float(y, &num);
	
	return num; 
  }
